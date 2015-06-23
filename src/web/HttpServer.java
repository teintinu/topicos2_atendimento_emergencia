package web;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;
import java.util.Date;

/**
 * Java HTTP Server
 *
 * This simple HTTP server supports GET and HEAD requests.
 *
 * @author Dustin R. Callaway
 */
public class HttpServer implements Runnable {
	// static constants
	// HttpServer root is the current directory
	static final int PORT = 8080; // default port

	// instance variables
	Socket connect;

	// constructor
	public HttpServer(Socket connect) {
		this.connect = connect;
	}

	private static boolean listenning = false;

	/**
	 * main method creates a new HttpServer instance for each request and starts
	 * it running in a separate thread.
	 */
	public static void start() {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("\nListening for connections on port " + PORT
					+ "...\n");
			listenning = true;
			while (listenning) // listen until user halts execution
			{
				HttpServer server = new HttpServer(serverConnect.accept()); // instantiate
																			// HttpServer
				// create new thread
				Thread threadRunner = new Thread(server);
				threadRunner.start(); // start thread
			}
		} catch (IOException e) {
			System.err.println("Server error: " + e);
		}
	}

	public static void stop() {
		listenning = false;
	}

	/**
	 * run method services each request in a separate thread.
	 */
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		String url = null;

		try {
			// get character input stream from client
			in = new BufferedReader(new InputStreamReader(
					connect.getInputStream(), StandardCharsets.UTF_8));
			// get character output stream to client (for headers)
			out = new PrintWriter(new OutputStreamWriter(
					connect.getOutputStream(), StandardCharsets.UTF_8), true);

			// get first line of request from client
			String input = in.readLine();
			// create StringTokenizer to parse request
			StringTokenizer parse = new StringTokenizer(input);
			// parse out method
			String method = parse.nextToken().toUpperCase();
			// parse out file requested
			url = parse.nextToken().toLowerCase();

			String response = getResponse(url).toString();

			FileInputStream fileIn = null;
			// create byte array to store file data
			byte[] fileData = new byte[response.length()];

			// send HTTP headers
			out.println("HTTP/1.0 200 OK");
			out.println("Server: Java HTTP Server 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: text/csv");
			out.println("Content-length: " + response.length());
			out.println(); // blank line between headers and content
			out.println(response);
			out.flush(); // flush character output stream buffer

		} catch (IOException ioe) {
			System.err.println("Server Error: " + ioe);
		} finally {
			close(in); // close character input stream
			close(out); // close character output stream
			close(connect); // close socket connection
		}
	}

	private StringBuilder getResponse(String url) {
		StringBuilder ret = new StringBuilder();
		if (url.matches("^mapa$"))
			mapa(ret);
		else
			ret.append("INVALIDO");
		return ret;
	}

	private void mapa(StringBuilder ret) {
		// TODO Auto-generated method stub

	}

	public void close(Object stream) {
		if (stream == null)
			return;

		try {
			if (stream instanceof Reader) {
				((Reader) stream).close();
			} else if (stream instanceof Writer) {
				((Writer) stream).close();
			} else if (stream instanceof InputStream) {
				((InputStream) stream).close();
			} else if (stream instanceof OutputStream) {
				((OutputStream) stream).close();
			} else if (stream instanceof Socket) {
				((Socket) stream).close();
			} else {
				System.err.println("Unable to close object: " + stream);
			}
		} catch (Exception e) {
			System.err.println("Error closing stream: " + e);
		}
	}
}