package com.example;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, Text> {

    private Map<String, Set<String>> docWordMap = new HashMap<>();

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Set<String> uniqueDocs = new HashSet<>();
        for (Text doc : values) {
            uniqueDocs.add(doc.toString());
        }

        for (String doc : uniqueDocs) {
            docWordMap.putIfAbsent(doc, new HashSet<>());
            docWordMap.get(doc).add(key.toString());
        }
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> docs = new ArrayList<>(docWordMap.keySet());

        for (int i = 0; i < docs.size(); i++) {
            for (int j = i + 1; j < docs.size(); j++) {
                String doc1 = docs.get(i);
                String doc2 = docs.get(j);

                Set<String> words1 = docWordMap.get(doc1);
                Set<String> words2 = docWordMap.get(doc2);

                Set<String> intersection = new HashSet<>(words1);
                intersection.retainAll(words2);

                Set<String> union = new HashSet<>(words1);
                union.addAll(words2);

                double similarity = (double) intersection.size() / union.size();

                context.write(new Text(doc1 + ", " + doc2), new Text("Similarity: " + String.format("%.2f", similarity)));
            }
        }
    }
}
