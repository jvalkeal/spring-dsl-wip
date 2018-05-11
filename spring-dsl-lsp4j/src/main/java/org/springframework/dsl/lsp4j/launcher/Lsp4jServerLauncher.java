/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.dsl.lsp4j.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.dsl.lsp.server.config.DslConfigurationProperties;

import reactor.core.Disposable;
import reactor.core.Disposables;

public class Lsp4jServerLauncher {

	private static final Logger log = LoggerFactory.getLogger(Lsp4jServerLauncher.class);

	private Disposable.Composite disposables = Disposables.composite();

	private final LanguageServer languageServer;
	private final DslConfigurationProperties dslConfigurationProperties;


	// @Value("${jdbc.driverClassName}")
	// -Dspring.lsp.client-port=45556 -Dserver.port=45556 -Dsts.lsp.client=vscode -Dsts.log.file=/tmp/vscode-simple-1526031253571.log

	// temporary hack to support vscode integration done in sts space
	@Value("${spring.lsp.client-port:}")
	private Integer clientPort;

	@Value("${server.port:}")
	private Integer serverPort;


	public Lsp4jServerLauncher(LanguageServer languageServer, DslConfigurationProperties dslConfigurationProperties) {
		this.languageServer = languageServer;
		this.dslConfigurationProperties = dslConfigurationProperties;
	}

	@PostConstruct
	public void start() throws Exception {
		run();
	}

	@PreDestroy
	public void stop() {
		log.info("Language Server shutting down");
		disposables.dispose();
	}

	protected static class Connection implements Disposable {
		final InputStream in;
		final OutputStream out;
		final Socket socket;

		private Connection(InputStream in, OutputStream out, Socket socket) {
			this.in = in;
			this.out = out;
			this.socket = socket;
		}

		@Override
		public void dispose() {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.warn("Connection input caused error", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					log.warn("Connection output caused error", e);
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					log.warn("Connection socket caused error", e);
				}
			}
		}
	}

	public void start(Integer clientPort) throws IOException {
		log.info("Starting LS");
		Connection connection = connectToClient(clientPort);
		disposables.add(connection);
		run(connection);
	}

	public void startAsSocketServer(int port) throws IOException {
		log.info("Starting LS as standlone server on port {}", port);

		Function<MessageConsumer, MessageConsumer> wrapper = consumer -> {
			MessageConsumer result = consumer;
			return result;
		};

		Launcher<LanguageClient> launcher = createSocketLauncher(
				languageServer,
				LanguageClient.class,
				new InetSocketAddress("localhost", port),
				createServerThreads(),
				wrapper
		);

		if (languageServer instanceof LanguageClientAware) {
			((LanguageClientAware)languageServer).connect(launcher.getRemoteProxy());
		}
		Future<Void> future = launcher.startListening();
		disposables.add(waitFor(future));
	}

    protected ExecutorService createServerThreads() {
		return Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "LSP4J");
			t.setDaemon(false); // maybe if we set this as false. We don't need our own async thread to avoid jvm shutting down
			return t;
		});
	}

	private Disposable waitFor(Future<Void> future) {
		return () -> {
			try {
				future.get(3, TimeUnit.SECONDS);
			} catch (Exception e) {
				log.warn("", e);
			}
		};
	}

	private <T> Launcher<T> createSocketLauncher(Object localService, Class<T> remoteInterface,
			SocketAddress socketAddress, ExecutorService executorService,
			Function<MessageConsumer, MessageConsumer> wrapper) throws IOException {
		AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(socketAddress);
		AsynchronousSocketChannel socketChannel;
		try {
			socketChannel = serverSocket.accept().get();
			return Launcher.createIoLauncher(localService, remoteInterface, Channels.newInputStream(socketChannel),
					Channels.newOutputStream(socketChannel), executorService, wrapper);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

    private Connection connectToClient(Integer port) throws IOException {
		if (port != null) {
			Socket socket = new Socket("localhost", port);

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			log.info("Connected to parent using socket on port {}", port);
			return new Connection(in, out, socket);
		}
		else {
			InputStream in = System.in;
			PrintStream out = System.out;

			log.info("Connected to parent using stdio");

			return new Connection(in, out, null);
		}
	}

	protected void run(Connection connection) {
		ExecutorService executor = createServerThreads();
		disposables.add(executor::shutdown);
		Function<MessageConsumer, MessageConsumer> wrapper = (MessageConsumer consumer) -> {
			return (msg) -> {
				try {
					consumer.consume(msg);
				} catch (UnsupportedOperationException e) {
					//log a warning and ignore. We are getting some messages from vsCode the server doesn't know about
					log.warn("Unsupported message was ignored!", e);
				}
			};
		};
		Launcher<LanguageClient> launcher = Launcher.createLauncher(languageServer,
				LanguageClient.class,
				connection.in,
				connection.out,
				executor,
				wrapper
		);

		if (languageServer instanceof LanguageClientAware) {
			LanguageClient client = launcher.getRemoteProxy();
			((LanguageClientAware) languageServer).connect(client);
		}

		disposables.add(waitFor(launcher.startListening()));
	}

	public void run() throws Exception {
		Integer serverPort = dslConfigurationProperties.getLsp().getServer().getPort();
		if (serverPort != null) {
			startAsSocketServer(serverPort);
		} else {
			Integer clientPort = dslConfigurationProperties.getLsp().getClient().getPort();
			if (this.clientPort != null) {
				clientPort = this.clientPort;
			}
			start(clientPort);
		}
	}

}
