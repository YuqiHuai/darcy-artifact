/**
 * Copyright 2017 K.Koike
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.seapanda.bunnyhop.programexecenv;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

/**
 * ローカルのアクセスのみ許すソケットを作成するファクトリ
 * @author K.Koike
 */
public class LocalServerSocketFactory implements RMIServerSocketFactory {
	
	private int localPort;
	private int id;	//!< 同一性確認のためのID
	
	/**
	 * コンストラクタ
	 * @param id オブジェクトの同一性確認のためのID
	 */
	public LocalServerSocketFactory(int id) {
		this.id = id;
	}
	
	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 0, InetAddress.getLoopbackAddress());
		}
		catch(IOException e) {
			throw new IOException();
		}
		localPort = serverSocket.getLocalPort();
		return serverSocket;
	}
	
	@Override
	public int hashCode() {
		return id;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return (getClass() == obj.getClass()) && (id == ((LocalServerSocketFactory)obj).id);
    }
	
	public int getLocalPort() {
		return localPort;
	}
}

