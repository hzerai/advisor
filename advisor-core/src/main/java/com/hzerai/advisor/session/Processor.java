/**
 * 
 */
package com.hzerai.advisor.session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import com.hzerai.advisor.data.AnalysisOutput;
import com.hzerai.advisor.data.Database;
import com.hzerai.advisor.data.TransientException;
import com.hzerai.advisor.data.mapping.ExceptionEvaluator;
import com.hzerai.advisor.data.mapping.ExceptionMapper;
import com.hzerai.advisor.data.mapping.MappingFactory;
import com.hzerai.advisor.exception.NotFoundException;
import com.hzerai.advisor.exception.ProcessException;
import com.hzerai.advisor.exception.SourceException;
import com.hzerai.advisor.parse.LogParser;
import com.hzerai.advisor.parse.ParserFactory;
import com.hzerai.advisor.source.LogSource;
import com.hzerai.advisor.source.SourceType;
import com.hzerai.advisor.source.internal.FileSource;
import com.hzerai.advisor.source.internal.FolderSource;
import com.hzerai.advisor.source.internal.ZipSource;

/**
 * @author Habib Zerai
 *
 */
public class Processor {

	private LogSource<?, ?> input;
	private String out = null;
	private LocalDateTime toDate;
	private LocalDateTime fromDate;
	private String exceptionName;
	private List<AnalysisOutput> output = new ArrayList<>();
	private InputStream is;
	public static String seperator = padRightSpaces(" ", 156, '=');
	private static final Logger LOG = Logger.getAnonymousLogger();
	private static final String LOGO = "\t          N	        NNNNNNNNNNNNNN      NN                 NN   NN   NNNNNNNNNNNN  NNNNNNNNNNNNNN    NNNNNNNNNNN   (TM)  \r\n"
			+ "\t         NNN           NN             NN     NN               NN    NN   NN            NN          NN    NN         NN\r\n"
			+ "\t        NN NN          NN              NN     NN             NN     NN   NN            NN          NN    NN          NN\r\n"
			+ "\t       NN   NN         NN               NN     NN           NN      NN   NN            NN          NN    NN          NN\r\n"
			+ "\t      NN     NN        NN                NN     NN         NN       NN   NN            NN          NN    NN         NN\r\n"
			+ "\t     NN       NN       NN                NN      NN       NN        NN   NNNNNNNNNNNN  NN          NN    NN       NN\r\n"
			+ "\t    NNNNNNNNNNNNN      NN                NN       NN     NN         NN             NN  NN          NN    NNNNNNNNN\r\n"
			+ "\t   NN           NN     NN               NN         NN   NN          NN             NN  NN          NN    NN       NN\r\n"
			+ "\t  NN             NN    NN              NN           NN NN           NN             NN  NN          NN    NN        NN\r\n"
			+ "\t NN               NN   NN             NN             NNN            NN             NN  NN          NN    NN         NN\r\n"
			+ "\tNN                 NN  NNNNNNNNNNNNNN                 N             NN   NNNNNNNNNNNN  NNNNNNNNNNNNNN    NN           NN\r\n";

	Processor(LogSource<?, ?> input, String out, LocalDateTime fromDate, LocalDateTime toDate) {
		this.input = input;
		this.out = out;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	Processor(LogSource<?, ?> input, String out, LocalDateTime fromDate, LocalDateTime toDate, String exceptionName) {
		this.input = input;
		this.out = out;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.exceptionName = exceptionName;
	}

	public Processor(InputStream is, LocalDateTime fd, LocalDateTime td) {
		this.is = is;
		this.fromDate = fd;
		this.toDate = td;
	}

	public Processor(InputStream is, LocalDateTime fd, LocalDateTime td, String exceptionName) {
		this.is = is;
		this.fromDate = fd;
		this.toDate = td;
		this.exceptionName = exceptionName;

	}

	void process() {
		LocalDateTime time = LocalDateTime.now();
		Logger.getLogger("Advisor").log(Level.INFO, "Process starting ");
		if (input != null) {
			process(input);
		} else if (is != null) {
			process(is);
		} else {
			throw new ProcessException("source is null.");
		}

		Logger.getLogger("Advisor").log(Level.INFO, "Process finished ");
		Logger.getLogger("Advisor").log(Level.INFO,
				"Process took " + (time.until(LocalDateTime.now(), ChronoUnit.SECONDS) + " seconds"));
	}

	private void process(LogSource<?, ?> genericSource) {
		try {
			SourceType sourceType = genericSource.getType();
			if (SourceType.File.equals(sourceType)) {
				process((FileSource) genericSource);
			} else if (SourceType.Folder.equals(sourceType)) {
				process((FolderSource) genericSource);
			} else if (SourceType.Zip.equals(sourceType)) {
				process((ZipSource) genericSource);
			}
		} catch (NotFoundException | SourceException e) {
			throw new ProcessException(e);
		}
	}

	private void process(ZipSource zipSource) throws NotFoundException, SourceException {
		Enumeration<? extends ZipEntry> entries = zipSource.read();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				try (InputStream stream = zipSource.getUnderlyingFile().getInputStream(entry)) {
					Logger.getLogger("Advisor").log(Level.INFO,
							"Found file : " + zipSource.getPath() + " - " + entry.getName());
					LogParser parser = ParserFactory.getParser(new String(stream.readAllBytes()));
					AnalysisOutput parseResult = parser.parse();
					parseResult.setLogfile(zipSource.getPath() + " - " + entry.getName());
					output.add(parseResult);
				} catch (IOException e) {
					throw new ProcessException(e);
				}
			}
		}
	}

	private void process(FolderSource folderSource) throws NotFoundException, SourceException {
		folderSource.read().forEach(this::process);
	}

	private void process(FileSource fileSource) throws NotFoundException, SourceException {
		LogParser parser = ParserFactory.getParser(fileSource.read());
		AnalysisOutput parseResult = parser.parse();
		parseResult.setLogfile(fileSource.getPath());
		output.add(parseResult);
	}

	private void process(InputStream is) {
		String input = "";
		try {
			input = new String(is.readAllBytes());
			is.close();
		} catch (IOException e) {
			throw new ProcessException(e);
		}

		LogParser parser = ParserFactory.getParser(input);
		AnalysisOutput parseResult = parser.parse();
		parseResult.setLogfile("Rest service");
		output.add(parseResult);
	}

	/**
	 * @param database
	 * @param out
	 */
	public void print(Database database, PrintStream out) {
		String output = map(database);
		output = System.lineSeparator() + LOGO + System.lineSeparator() + output;
		out.println(output);
		if (this.out != null) {
			try {
				File file = new File(this.out);
				file.createNewFile();
				try (FileOutputStream fos = new FileOutputStream(file)) {
					fos.write(output.getBytes());
					fos.flush();
				}
				System.out.println();
				System.out.println("\t Output file is : " + file.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public List<AnalysisOutput> getOutput() {
		return Collections.unmodifiableList(output);
	}

	public String map(Database db) {
		List<ExceptionEvaluator> exceptions = new ArrayList<>();
		int knowException = 0;
		int allException = 0;
		String seperator2 = padRightSpaces("", 156, '|');
		final ExceptionMapper mapper = MappingFactory.getMapper(db);
		StringBuilder sb = new StringBuilder();
		for (AnalysisOutput out : output) {
			sb.append(System.lineSeparator()).append("\t");
			sb.append(System.lineSeparator()).append("\t");
			sb.append(seperator.substring(44));
			sb.append(System.lineSeparator()).append("\t");
			sb.append(seperator2.substring(44));
			sb.append(System.lineSeparator()).append("\t");
			sb.append(seperator.substring(44));
			sb.append(System.lineSeparator()).append("\t");
			sb.append(padRightSpaces("||||||||||||||||||||               " + out.getLogFile() + "                ", 112,
					'|')).append(System.lineSeparator()).append("\t");
			sb.append(seperator.substring(44));
			sb.append(System.lineSeparator()).append("\t");
			sb.append(seperator2.substring(44));
			sb.append(System.lineSeparator()).append("\t");
			for (TransientException er : out.getResult()) {
				if (er.getName() == null && er.getCausedBy().isEmpty()) {
					continue;
				}
				if (er.getDate() != null) {
					if ((this.fromDate != null && !er.getDate().isAfter(this.fromDate))
							|| (this.toDate != null && !er.getDate().isBefore(this.toDate))) {
						continue;
					}
				}
				exceptions.add(mapper.map(er));
			}
			exceptions = clean(exceptions);
			for (ExceptionEvaluator me : exceptions) {
				allException++;
				if (me.isMapped()) {
					knowException++;
				}
				sb.append(me).append(System.lineSeparator()).append("\t");
			}
			sb.append(seperator.substring(44)).append(System.lineSeparator()).append(System.lineSeparator());
			exceptions.clear();
		}
		sb.insert(0, System.lineSeparator());
		sb.insert(0, seperator);
		sb.insert(0, System.lineSeparator());
		sb.insert(0, seperator);
		sb.insert(0, System.lineSeparator());
		sb.insert(0, String.format(
				" |||||||||||||||||||||            Advisor scanned %s files and found %s known exceptions from a total of %s exceptions.            |||||||||||||||||||||",
				output.size(), knowException, allException));
		sb.insert(0, System.lineSeparator());
		sb.insert(0, seperator);
		sb.insert(0, System.lineSeparator());
		sb.insert(0, seperator);
		sb.insert(0, System.lineSeparator());
		sb.insert(0, System.lineSeparator());
		return sb.toString();
	}

	public List<ExceptionEvaluator> mapToList(Database db) {
		List<ExceptionEvaluator> exceptions = new ArrayList<>();
		final ExceptionMapper mapper = MappingFactory.getMapper(db);
		for (AnalysisOutput out : output) {
			for (TransientException er : out.getResult()) {
				if (er.getName() == null && er.getCausedBy().isEmpty()) {
					continue;
				}
				if (exceptionName != null && !(er.getName().contains(exceptionName)
						|| er.getCausedBy().stream().anyMatch(e -> e.contains(exceptionName)))) {
					continue;
				}
				if (er.getDate() != null) {
					if ((this.fromDate != null && !er.getDate().isAfter(this.fromDate))
							|| (this.toDate != null && !er.getDate().isBefore(this.toDate))) {
						continue;
					}
				}
				exceptions.add(mapper.map(er));
			}
		}
		return clean(exceptions);
	}

	public List<ExceptionEvaluator> clean(List<ExceptionEvaluator> set) {
		return set.stream().collect(Collectors.toMap(this::createKey, Function.identity(), (old_, new_) -> {
			((ExceptionEvaluator) new_).setChild((ExceptionEvaluator) old_);
			return new_;
		})).values().stream().sorted((e1, e2) -> e1.getException().getDate().compareTo(e2.getException().getDate()))
				.collect(Collectors.toList());
	}

	private String createKey(ExceptionEvaluator ex) {
		if (ex.getException().getMessage().equals("") || ex.getException().getMessage().matches("null|empty")) {
			return ex.getException().getStackTrace();
		}
		return ex.getException().getMessage();
	}

	public String padRightSpaces(Object input, int padSize) {
		return padRightSpaces(input, padSize, ' ');
	}

	public static String padRightSpaces(Object input, int padSize, char ch) {
		String inputString = String.valueOf(input);
		if (inputString.length() >= padSize) {
			return inputString;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(inputString);
		while (sb.length() < padSize) {
			sb.append(ch);
		}
		return sb.toString();
	}
}
