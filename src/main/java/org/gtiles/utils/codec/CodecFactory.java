package org.gtiles.utils.codec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CodecFactory {

	public static final String transcodingToolsPath = "E:\\workspace\\ffmpeg\\bin\\ffmpeg";

	private List<String> codecCommand = new ArrayList<String>();

	private static Map<String, Process> codecProcessPool = new LinkedHashMap<String, Process>();

	public enum CodecCommand {

		/** 输入视频地址 */
		INPUT("-i"),
		/** 转化码率 */
		BV("-b:v"),
		/** 码率控制缓冲器的大小，默认与码率一致 */
		BUFSIZE("-bufsize"),
		/* 视频编码格式转换 **/
		VCODEC("-vcodec"),
		/* 音频编码格式转换 **/
		ACODEC("-acodec"),
		/** 裁剪 开始时间 */
		CUT_START("-ss"),
		/** 裁剪 长度 */
		CUT_LENGTH("-t");
		// ,CUT("-ss {start} -t {length} -vcodec {}") ;

		private String command;

		CodecCommand(String command) {
			this.command = command;
		}

		public String getCommand() {
			return command;
		}
	}

	public static CodecFactory getInstance() {
		return new CodecFactory();
	}

	private CodecFactory() {
		codecCommand.add(transcodingToolsPath);
	}

	private CodecFactory(String inputFilePath) {
		codecCommand.add(transcodingToolsPath);
		codecCommand.add(CodecCommand.INPUT.getCommand());
		codecCommand.add(inputFilePath);
	}

	public CodecFactory with(CodecCommand command, String value) {
		if (!codecCommand.contains(command.getCommand()) && value != null) {
			codecCommand.add(command.getCommand());
			codecCommand.add(value);
		}
		return this;
	}

	public void transcodingTo(String outputFilePath) {
		codecCommand.add(outputFilePath);
		asyncExecuteCommand();

	}

	public void cut(String start, String length, String outputFilePath) {
		with(CodecCommand.CUT_START, start);
		with(CodecCommand.CUT_LENGTH, length);
		with(CodecCommand.VCODEC, "copy");
		with(CodecCommand.ACODEC, "copy");
		codecCommand.add(outputFilePath);
		asyncExecuteCommand();
	}

	private void asyncExecuteCommand() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// Process pro =
					// Runtime.getRuntime().exec(codecCommand.toArray(new
					// String[0]));
					// codecProcessPool.put("1", pro);
					// // 如果不读取流则targetFile.exists() 文件不存在，但是程序没有问题
					// BufferedReader bufferedReader = new BufferedReader(new
					// InputStreamReader(pro.getInputStream()));
					// String line;
					// while ((line = bufferedReader.readLine()) != null) {
					// System.out.println(line);
					// }
					// pro.waitFor();
					// pro.exitValue();
					Process videoProcess = new ProcessBuilder(codecCommand).start();
//					String line;
//					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(videoProcess.getInputStream()));
//					while ((line = bufferedReader.readLine()) != null) {
//						System.out.println("-------------");
//						System.out.println(line);
//					}
					new PrintStream(videoProcess.getErrorStream()).start();
					new PrintStream(videoProcess.getInputStream()).start();
					videoProcess.waitFor();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static void main(String[] args) throws Exception {
		// 视频转码压缩
		CodecFactory.getInstance().with(CodecCommand.INPUT, "J:\\培训资料\\培训视频\\微信开发\\klx-wx_mp-dev.MP4")
				.with(CodecCommand.BV, "600k").with(CodecCommand.BUFSIZE, "600k").with(CodecCommand.VCODEC, "h264")
				.transcodingTo("J:\\培训资料\\培训视频\\微信开发\\klx-wx_mp-dev-600k.mp4");
	}
}

class PrintStream extends Thread {
	java.io.InputStream __is = null;

	public PrintStream(java.io.InputStream is) {
		__is = is;
	}

	public void run() {
		try {
			while (this != null) {
				int _ch = __is.read();
				if (_ch != -1)
					System.out.print((char) _ch);
				else
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
