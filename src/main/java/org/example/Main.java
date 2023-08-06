package org.example;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        // Specify the analyzer for tokenizing text
        // The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();

        // create the index
        Directory index = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        addDoc(w, "samsung galaxy note", "1");
        addDoc(w, "apple iphone", "2");
        addDoc(w, "nothing phone 2", "3");
        addDoc(w, "motorola edge 30", "4");
        addDoc(w, "apple airpod", "5");
        addDoc(w, "apple macbook", "6");
        w.close();

        // 2. query
        String queryString = args.length > 0 ? args[0] : "appel"; // user input with one miss aligned character

        // Approximate search using Levenshtein distance algorithm
        Query query = new FuzzyQuery(new Term("product", queryString),2);

        // 3. search
        int hitLimit = 2;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(query, hitLimit);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("id") + "\t" + d.get("product"));
        }

        // reader can only be closed when there
        // is no need to access the documents anymore.
        reader.close();
    }

    private static void addDoc(IndexWriter w, String product, String id) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("product", product, Field.Store.YES));

        // use a string field for id because we don't want it tokenized
        doc.add(new StringField("id", id, Field.Store.YES));
        w.addDocument(doc);
    }
}
