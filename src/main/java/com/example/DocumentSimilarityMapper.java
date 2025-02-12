package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, Text> {

    private Text docName = new Text();
    private Text word = new Text();

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (!line.isEmpty()) {
            String[] parts = line.split("\\s+", 2);
            if (parts.length == 2) {
                docName.set(parts[0]);  // Extract document name (e.g., "Document1")
                StringTokenizer tokenizer = new StringTokenizer(parts[1]);

                while (tokenizer.hasMoreTokens()) {
                    word.set(tokenizer.nextToken().replaceAll("[^a-zA-Z]", "").toLowerCase());
                    context.write(word, docName);
                }
            }
        }
    }
}
