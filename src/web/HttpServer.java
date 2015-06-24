package web;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.StringTokenizer;

import environment.Cidade;

/**
 * Java HTTP Server
 *
 * This simple HTTP server supports GET and HEAD requests.
 *
 * @author Dustin R. Callaway
 */
public class HttpServer implements Runnable {
	static final String WEB_ROOT = ".";
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
		String url = null;

		OutputStream out=null;
		try {
			in = new BufferedReader(new InputStreamReader(
					connect.getInputStream(), StandardCharsets.UTF_8));
			out = connect.getOutputStream();

			String input = in.readLine();
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase();
			url = parse.nextToken().toLowerCase();

			if (url.matches("^mapa$"))
				renderMapa(out);
			else
				serverFile(url, out);

		} catch (IOException ioe) {
			System.err.println("Server Error: " + ioe);
		} finally {
			close(in); // close character input stream
			close(out); // close character output stream
			close(connect); // close socket connection
		}
	}

	private void serverFile(String fileRequested, OutputStream outputStream) {
		PrintWriter out = new PrintWriter(outputStream);
		BufferedOutputStream dataOut = new BufferedOutputStream(outputStream);

		if (fileRequested.endsWith("/")) {
			// append default file name to request
			fileRequested += "index.html";
		}

		// create file object
		File file = new File(WEB_ROOT, fileRequested);
		// get length of file
		int fileLength = (int) file.length();

		// get the file's MIME content type
		String content = getContentType(fileRequested);
		try {
			FileInputStream fileIn = null;
			// create byte array to store file data
			byte[] fileData = new byte[fileLength];

			try {
				// open input stream from file
				fileIn = new FileInputStream(file);
				// read file into byte array
				fileIn.read(fileData);
			} finally {
				close(fileIn); // close file input stream
			}

			// send HTTP headers
			out.println("HTTP/1.0 200 OK");
			out.println("Server: Java HTTP Server 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: " + content);
			out.println("Content-length: " + file.length());
			out.println(); // blank line between headers and content
			out.flush(); // flush character output stream buffer

			dataOut.write(fileData, 0, fileLength); // write file
			dataOut.flush(); // flush binary output stream buffer

		} catch (Exception e) {
			System.err.println("Server Error: " + e);
		}
	}

	private void renderMapa(OutputStream outputStream) {
		StringBuilder ret = new StringBuilder();
		Cidade.singleton.toCSV(ret);
		outputString(outputStream, "text/csv", ret.toString());
	}

	private void outputString(OutputStream outputStream, String mime,
			String response) {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(outputStream,
				StandardCharsets.UTF_8), true);
		out.println("HTTP/1.0 200 OK");
		out.println("Server: Java HTTP Server 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + mime);
		out.println("Content-length: " + response.length());
		out.println(); // blank line between headers and content
		out.println(response);
		out.flush();
		out.close();
	}

	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
			return "text/html";
		} else if (fileRequested.endsWith(".css")) {
			return "text/css";
		} else if (fileRequested.endsWith(".js")) {
			return "application/ecmascript";
		} else if (fileRequested.endsWith(".gif")) {
			return "image/gif";
		} else if (fileRequested.endsWith(".jpg")
				|| fileRequested.endsWith(".jpeg")) {
			return "image/jpeg";
		} else {
			return "text/plain";
		}
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