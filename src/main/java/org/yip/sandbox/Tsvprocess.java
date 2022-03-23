package org.yip.sandbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.yipuran.csv.BOMfunction;
import org.yipuran.csv4j.CSVReader;
import org.yipuran.csv4j.CSVStreamProcessor;
import org.yipuran.csv4j.ParseException;
import org.yipuran.csv4j.ProcessingException;

/**
 * Tsvprocess
 */
public class Tsvprocess extends CSVStreamProcessor{
	private boolean blankIsNull = false;
	/**
	 * デフォルトコンストラクタ.
	 * ブランク、",," は、null にしないで、空文字として読み込む。
	 */
	public Tsvprocess(){}

	/**
	 * ブランク→null指定コンストラクタ.
	 * @param true=ブランク、",," は、null として読み込む。
	 * @since 4.18
	 */
	public Tsvprocess(boolean blankIsNull) {
		this.blankIsNull = blankIsNull;
	}

	/**
	 * ヘッダ有りＴＳＶ読込み実行.
	 * @param inReader InputStreamReader
	 * @param header ヘッダ行 Consumer
	 * @param processor コンテンツ行BiConsumer、CSV行読込みカウント（１始まり）とTSV文字列のList
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public void read(InputStreamReader inReader, Consumer<List<String>> header, BiConsumer<Integer, List<String>> processor)
	throws IOException, ProcessingException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), '\t', getComment(), blankIsNull);
		try{
			int lineCount = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (isHasHeader() && lineCount==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
						header.accept(fields);
					}else{
						processor.accept(lineCount, fields);
					}
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineCount++;
			}
		}finally{
			reader.close();
		}
	}
	/**
	 * ヘッダ無しＴＳＶ読込み実行.
	 * @param inReader InputStreamReader
	 * @param processor BiConsumer 行のindexとTSV文字列のList
	 * @throws IOException
	 * @throws ProcessingException
	 * @throws ParseException
	 */
	public void readNoheader(InputStreamReader inReader, BiConsumer<Integer, List<String>> processor) throws IOException, ProcessingException, ParseException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), '\t', getComment(), blankIsNull);
		try{
			int lineIndex = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (lineIndex==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
					}
					processor.accept(lineIndex, fields);
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineIndex++;
			}
		}finally{
			reader.close();
		}
	}

	/**
	 * ヘッダ有りＴＳＶ読込み実行（Map形式読込み）.
	 * <PRE>
	 * ヘッダ行列をキーとして読込み結果をMapで実行
	 * </PRE>
	 * @param inReader InputStreamReader
	 * @param processor コンテンツ行BiConsumer、TSV行読込みカウント（１始まり）とヘッダのキーに対するコンテンツ行の値のMap
	 * @throws IOException
	 * @throws ProcessingException
	 */
	public void read(InputStreamReader inReader, BiConsumer<Integer, Map<String, String>> processor) throws IOException, ProcessingException{
		CSVReader reader = new CSVReader(new BufferedReader(inReader), Charset.forName(inReader.getEncoding()), '\t', getComment(), blankIsNull);
		try{
			Map<Integer, String> headerMap = new HashMap<>();
			int lineCount = 0;
			while(true){
				try{
					List<String> fields = reader.readLine();
					if (fields.size()==0) break;
					if (isHasHeader() && lineCount==0){
						String rep = fields.get(0);
						if (BOMfunction.match(rep)) {
							fields.remove(0);
							fields.add(0, BOMfunction.chop(rep));
						}
						int i = 0;
						for(String key:fields){
							headerMap.put(i, key);
							i++;
						}
					}else{
						processor.accept(lineCount,
							Stream.iterate(0, i->i+1).limit(fields.size())
							.collect(HashMap<String, String>::new, (r, t)->r.put(headerMap.get(t), fields.get(t)), (r, t)->{})
						);
					}
				}catch(Exception e){
					throw new ProcessingException(e, reader.getLineNumber());
				}
				lineCount++;
			}
		}finally{
			reader.close();
		}
	}

	/**
	 * ヘッダ有りＴＳＶ読込み実行.
	 * @param in InputStream
	 * @param charset 文字コード
	 * @param header ヘッダ行 Consumer
	 * @param processor コンテンツ行BiConsumer、TSV行読込みカウント（１始まり）とCSV文字列のList
	 * @throws IOException
	 * @throws ProcessingException
	 * @since 1.1
	 */
	public void read(InputStream in, Charset charset, Consumer<List<String>> header, BiConsumer<Integer, List<String>> processor) throws IOException, ProcessingException {
		read(new InputStreamReader(in, charset), header, processor);
	}
	/**
	 * ヘッダ無しＴＳＶ読込み実行.
	 * @param in InputStream
	 * @param charset 文字コード
	 * @param processor BiConsumer 行のindexとCSV文字列のList
	 * @throws IOException
	 * @throws ProcessingException
	 * @throws ParseException
	 * @since 1.1
	 */
	public void readNoheader(InputStream in, Charset charset, BiConsumer<Integer, List<String>> processor) throws IOException, ProcessingException {
		readNoheader(new InputStreamReader(in, charset), processor);
	}
	/**
	 * ヘッダ有りＴＳＶ読込み実行（Map形式読込み）.
	 * <PRE>
	 * ヘッダ行列をキーとして読込み結果をMapで実行
	 * </PRE>
	 * @param in InputStream
	 * @param charset 文字コード
	 * @param processor コンテンツ行BiConsumer、TSV行読込みカウント（１始まり）とヘッダのキーに対するコンテンツ行の値のMap
	 * @throws IOException
	 * @throws ProcessingException
	 * @since 1.1
	 */
	public void read(InputStream in, Charset charset, BiConsumer<Integer, Map<String, String>> processor) throws IOException, ProcessingException {
		read(new InputStreamReader(in, charset), processor);
	}
}
