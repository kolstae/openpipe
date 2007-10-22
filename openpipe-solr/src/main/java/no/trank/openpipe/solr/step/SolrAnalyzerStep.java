package no.trank.openpipe.solr.step;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.TokenFilterFactory;
import org.apache.solr.analysis.TokenizerFactory;

import no.trank.openpipe.api.BasePipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.PipelineStepStatus;
import no.trank.openpipe.api.PipelineStepStatusCode;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.solr.analysis.AnnotationTokenStream;
import no.trank.openpipe.solr.analysis.TokenStreamAnnotation;

/**
 * @version $Revision$
 */
public class SolrAnalyzerStep extends BasePipelineStep {
   private TokenizerFactory tokenizerFactory;
   private List<TokenFilterFactory> filterFactories = Collections.emptyList();
   private String fieldName;
   private Set<String> annotations = Collections.emptySet();

   /**
    * Constructs a <tt>SolrAnalyzerStep</tt> with name &quot;SolrAnalyzerStep&quot;
    */
   public SolrAnalyzerStep() {
      super("SolrAnalyzerStep");
   }

   public PipelineStepStatus execute(Document doc) throws PipelineException {
      try {
         final List<AnnotatedField> values = doc.getFields(fieldName);
         for (AnnotatedField value : values) {
            processFilters(value);
         }
      } catch (IOException e) {
         throw new PipelineException(e);
      }
      return new PipelineStepStatus(PipelineStepStatusCode.CONTINUE);
   }

   private void processFilters(AnnotatedField value) throws IOException {
      final TokenStream stream;
      if (tokenizerFactory != null) {
         stream = tokenizerFactory.create(new StringReader(value.getValue()));
      } else {
         stream = new AnnotationTokenStream(value, annotations);
      }
      final TokenStreamAnnotation annotation = new TokenStreamAnnotation(createFilters(stream));
      annotation.process();
      final Map<String, List<Annotation>> annotations = annotation.getAnnotations();
      for (Map.Entry<String, List<Annotation>> e : annotations.entrySet()) {
         value.set(e.getKey(), e.getValue());
      }
   }

   private TokenStream createFilters(TokenStream stream) {
      TokenStream filter = stream;
      for (TokenFilterFactory factory : filterFactories) {
         filter = factory.create(filter);
      }
      return filter;
   }

   @Override
   public void prepare() throws PipelineException {
      if (fieldName == null) {
         throw new PipelineException("fieldName cannot be null");
      }
      if (tokenizerFactory == null && filterFactories.isEmpty()) {
         throw new PipelineException("Either tokenizerFactory or filterFactories must be provided");
      }
   }

   @Override
   public void finish(boolean success) throws PipelineException {
   }

   public String getRevision() {
      return "$Revision$";
   }

   public TokenizerFactory getTokenizerFactory() {
      return tokenizerFactory;
   }

   public void setTokenizerFactory(TokenizerFactory tokenizerFactory) {
      this.tokenizerFactory = tokenizerFactory;
   }

   public List<TokenFilterFactory> getFilterFactories() {
      return filterFactories;
   }

   public void setFilterFactories(List<TokenFilterFactory> filterFactories) {
      this.filterFactories = filterFactories;
   }

   public String getFieldName() {
      return fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public Set<String> getAnnotations() {
      return annotations;
   }

   public void setAnnotations(Set<String> annotations) {
      this.annotations = annotations;
   }
}
