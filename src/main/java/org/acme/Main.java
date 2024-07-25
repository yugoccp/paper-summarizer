package org.acme;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.query.Query;
import org.acme.factories.AiModelFactory;
import org.acme.factories.ContentRetrieverFactory;
import org.acme.factories.EmbeddingFactory;
import org.acme.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    record DocumentConfig(String filename, List<TextSegment> segments){}

    public static void main(String[] args) {
        var dirPath = Path.of(System.getenv("DOCUMENT_DIR"));
        var chatModel = AiModelFactory.createChatModel();
        var embeddingModel = EmbeddingFactory.createEmbeddingModel();

        var documentConfigs = getDocumentConfigs(dirPath);

        var systemMessage = SystemMessage.from("""
                You are a professional scientific paper reader.
                
                Given a set of chunks of a scientific paper, extract the information below:
                - methodology: summary of the pre-processing methodology;
                - software: a list of software used for analysis;
                - algorithms: a list of AI models, algorithms or techniques;
                
                Follow the instructions below:
                1. Ensure to keep context of the original context.
                2. Keep the summary brief and to the point, avoiding unnecessary details.
                3. Write the summary in brazilian portuguese.
                4. Reply strictly following the `response_format`, without any additional comments.
                
                <response_format>
                ### Metodologia
                {methodology content}
                
                ### Softwares
                {software content}
                
                ### Algoritmos
                {algorithms content}
                </response_format>
                """);


        var resultPath = dirPath.resolve( "result.md");

        documentConfigs.forEach(docConfig -> {

            var embeddignStore = EmbeddingFactory.createEmbeddingStore();
            var segments = docConfig.segments();

            embeddignStore.addAll(embeddingModel.embedAll(segments).content(), segments);

            var contentRetriever = ContentRetrieverFactory.createContentRetriever(embeddingModel, embeddignStore);

            var context = Stream.of(
                    "Pre-processing",
                            "Methodology",
                            "Software",
                            "Library",
                            "AI model",
                            "Machine Learning")
                    .map(Query::from)
                    .map(contentRetriever::retrieve)
                    .flatMap(Collection::stream)
                    .map(Content::textSegment)
                    .map(TextSegment::text)
                    .collect(Collectors.joining("\n"));

            var resp = chatModel.generate(systemMessage, UserMessage.from(context)).content().text();
            var resultRow = """
                    ## %s
                    
                    %s
                    """.formatted(docConfig.filename(), resp);
            FileUtils.append(resultPath, resultRow);
        });
    }

    private static List<DocumentConfig> getDocumentConfigs(Path dirPath) {
        try (var dirStream = Files.newDirectoryStream(dirPath)) {
            var splitter = DocumentSplitters.recursive(500, 100);
            var documentConfigs = new ArrayList<DocumentConfig>();
            dirStream.forEach(path -> {
                logger.info(path.toString());
                var fileName = path.getFileName().toString();
                if(fileName.endsWith(".pdf")) {
                    var document = FileSystemDocumentLoader.loadDocument(path, new ApachePdfBoxDocumentParser());
                    var segments = splitter.split(document);
                    documentConfigs.add(new DocumentConfig(fileName, segments));
                }
            });
            return documentConfigs;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
