package com.wordplay.unit.starter.util.base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhuangpf
 * @date 2023-04-22
 */
public class IOUtil {

	public static final char DIR_SEPARATOR_UNIX = '/';
	public static final char DIR_SEPARATOR_WINDOWS = '\\';
	public static final char DIR_SEPARATOR;
	public static final String LINE_SEPARATOR_UNIX = "\n";
	public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
	public static final String LINE_SEPARATOR;
	private static final int DEFAULT_BUFFER_SIZE = 4096;

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy((InputStream) input, (OutputStream) output);
		return output.toByteArray();
	}

	public static byte[] toByteArray(Reader input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy((Reader) input, (OutputStream) output);
		return output.toByteArray();
	}

	public static byte[] toByteArray(Reader input, String encoding) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy((Reader) input, (OutputStream) output, encoding);
		return output.toByteArray();
	}

	public static char[] toCharArray(InputStream is) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy((InputStream) is, (Writer) output);
		return output.toCharArray();
	}

	public static char[] toCharArray(InputStream is, String encoding) throws IOException {
		CharArrayWriter output = new CharArrayWriter();
		copy((InputStream) is, (Writer) output, encoding);
		return output.toCharArray();
	}

	public static char[] toCharArray(Reader input) throws IOException {
		CharArrayWriter sw = new CharArrayWriter();
		copy((Reader) input, (Writer) sw);
		return sw.toCharArray();
	}

	public static String toString(InputStream input) throws IOException {
		StringWriter sw = new StringWriter();
		copy((InputStream) input, (Writer) sw);
		return sw.toString();
	}

	public static String toString(InputStream input, String encoding) throws IOException {
		StringWriter sw = new StringWriter();
		copy((InputStream) input, (Writer) sw, encoding);
		return sw.toString();
	}

	public static String toString(Reader input) throws IOException {
		StringWriter sw = new StringWriter();
		copy((Reader) input, (Writer) sw);
		return sw.toString();
	}

	public static List<String> readLines(InputStream input) throws IOException {
		InputStreamReader reader = new InputStreamReader(input);
		return readLines((Reader) reader);
	}

	public static List<String> readLines(InputStream input, String encoding) throws IOException {
		if (encoding == null) {
			return readLines(input);
		} else {
			InputStreamReader reader = new InputStreamReader(input, encoding);
			return readLines((Reader) reader);
		}
	}

	public static List<String> readLines(Reader input) throws IOException {
		BufferedReader reader = new BufferedReader(input);
		List<String> list = new ArrayList();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			list.add(line);
		}
		return list;
	}

	public static InputStream toInputStream(String input) {
		byte[] bytes = input.getBytes();
		return new ByteArrayInputStream(bytes);
	}

	public static InputStream toInputStream(String input, String encoding) throws IOException {
		byte[] bytes = encoding != null ? input.getBytes(encoding) : input.getBytes();
		return new ByteArrayInputStream(bytes);
	}

	public static void write(byte[] data, OutputStream output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	public static void write(byte[] data, Writer output) throws IOException {
		if (data != null) {
			output.write(new String(data));
		}
	}

	public static void write(byte[] data, Writer output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(new String(data, encoding));
			}
		}
	}

	public static void write(char[] data, Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	public static void write(char[] data, OutputStream output) throws IOException {
		if (data != null) {
			output.write((new String(data)).getBytes());
		}
	}

	public static void write(char[] data, OutputStream output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write((new String(data)).getBytes(encoding));
			}
		}
	}

	public static void write(String data, Writer output) throws IOException {
		if (data != null) {
			output.write(data);
		}
	}

	public static void write(String data, OutputStream output) throws IOException {
		if (data != null) {
			output.write(data.getBytes());
		}
	}

	public static void write(String data, OutputStream output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(data.getBytes(encoding));
			}
		}
	}

	public static void write(StringBuffer data, Writer output) throws IOException {
		if (data != null) {
			output.write(data.toString());
		}
	}

	public static void write(StringBuffer data, OutputStream output) throws IOException {
		if (data != null) {
			output.write(data.toString().getBytes());
		}
	}

	public static void write(StringBuffer data, OutputStream output, String encoding) throws IOException {
		if (data != null) {
			if (encoding == null) {
				write(data, output);
			} else {
				output.write(data.toString().getBytes(encoding));
			}
		}
	}

	public static void writeLines(Collection<String> lines, String lineEnding, OutputStream output) throws IOException {
		if (lines != null) {
			if (lineEnding == null) {
				lineEnding = LINE_SEPARATOR;
			}
			for (Iterator var3 = lines.iterator(); var3.hasNext(); output.write(lineEnding.getBytes())) {
				String line = (String) var3.next();
				if (line != null) {
					output.write(line.toString().getBytes());
				}
			}
		}
	}

	public static void writeLines(Collection<String> lines, String lineEnding, OutputStream output, String encoding) throws IOException {
		if (encoding == null) {
			writeLines(lines, lineEnding, output);
		} else {
			if (lines == null) {
				return;
			}
			if (lineEnding == null) {
				lineEnding = LINE_SEPARATOR;
			}
			for (Iterator var4 = lines.iterator(); var4.hasNext(); output.write(lineEnding.getBytes(encoding))) {
				String line = (String) var4.next();
				if (line != null) {
					output.write(line.toString().getBytes(encoding));
				}
			}
		}
	}

	public static void writeLines(Collection<String> lines, String lineEnding, Writer writer) throws IOException {
		if (lines != null) {
			if (lineEnding == null) {
				lineEnding = LINE_SEPARATOR;
			}
			for (Iterator var3 = lines.iterator(); var3.hasNext(); writer.write(lineEnding)) {
				String line = (String) var3.next();
				if (line != null) {
					writer.write(line.toString());
				}
			}
		}
	}

	public static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		return count > 2147483647L ? -1 : (int) count;
	}

	public static long copyLarge(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096];
		long count = 0L;
		int n;
		for (boolean var5 = false; -1 != (n = input.read(buffer)); count += (long) n) {
			output.write(buffer, 0, n);
		}
		return count;
	}

	public static void copy(InputStream input, Writer output) throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy((Reader) in, (Writer) output);
	}

	public static void copy(InputStream input, Writer output, String encoding) throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy((Reader) in, (Writer) output);
		}
	}

	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		return count > 2147483647L ? -1 : (int) count;
	}

	public static long copyLarge(Reader input, Writer output) throws IOException {
		char[] buffer = new char[4096];
		long count = 0L;
		int n;
		for (boolean var5 = false; -1 != (n = input.read(buffer)); count += (long) n) {
			output.write(buffer, 0, n);
		}
		return count;
	}

	public static void copy(Reader input, OutputStream output) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(output);
		copy((Reader) input, (Writer) out);
		out.flush();
	}

	public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			OutputStreamWriter out = new OutputStreamWriter(output, encoding);
			copy((Reader) input, (Writer) out);
			out.flush();
		}
	}

	public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
		if (!(input1 instanceof BufferedInputStream)) {
			input1 = new BufferedInputStream((InputStream) input1);
		}
		if (!(input2 instanceof BufferedInputStream)) {
			input2 = new BufferedInputStream((InputStream) input2);
		}
		int ch2;
		for (int ch = ((InputStream) input1).read(); -1 != ch; ch = ((InputStream) input1).read()) {
			ch2 = ((InputStream) input2).read();
			if (ch != ch2) {
				return false;
			}
		}
		ch2 = ((InputStream) input2).read();
		return ch2 == -1;
	}

	public static boolean contentEquals(Reader input1, Reader input2) throws IOException {
		if (!(input1 instanceof BufferedReader)) {
			input1 = new BufferedReader((Reader) input1);
		}
		if (!(input2 instanceof BufferedReader)) {
			input2 = new BufferedReader((Reader) input2);
		}
		int ch2;
		for (int ch = ((Reader) input1).read(); -1 != ch; ch = ((Reader) input1).read()) {
			ch2 = ((Reader) input2).read();
			if (ch != ch2) {
				return false;
			}
		}
		ch2 = ((Reader) input2).read();
		return ch2 == -1;
	}

	static {
		DIR_SEPARATOR = File.separatorChar;
		StringWriter buf = new StringWriter(4);
		PrintWriter out = new PrintWriter(buf);
		out.println();
		LINE_SEPARATOR = buf.toString();
	}

}
