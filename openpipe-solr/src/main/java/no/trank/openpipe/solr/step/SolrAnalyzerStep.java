/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import no.trank.openpipe.api.MultiInputFieldPipelineStep;
import no.trank.openpipe.api.PipelineException;
import no.trank.openpipe.api.document.AnnotatedField;
import no.trank.openpipe.api.document.Annotation;
import no.trank.openpipe.api.document.Document;
import no.trank.openpipe.config.annotation.NotNull;
import no.trank.openpipe.solr.analysis.AnnotationTokenStream;
import no.trank.openpipe.solr.analysis.TokenStreamAnnotation;

/**
 * A <tt>PipelineStep</tt> for running solr tokenizer-/analyzer-chains in OpenPipe.<br/>
 * <br/>
 * Similar to the SolR config, this should have one or zero TokenizerFactories, and zero or more 
 * TokenFilterFactories.<br/>
 * <br/>
 * As an alternative to specifying a TokenizerFactory, you may set the annotation set. (If you use another tokenizer,
 * but want to use filters from SolR). If this option is used, annotated tokens of a type in the annotation set will be
 * treated as a token.<br/>
 * <br/>
 * If you want to tokenize in the pipeline, you can look into the schema.xml for solr to get tips on how to set up this.<br/>
 * <br/>
 * If you are using spring to supply the TokenFilterFactories, you can use
 * <tt>no.trank.openpipe.solr.util.TokenFilterFactoryFactory</tt> to initialize the filterfactories.
 *
 * @see no.trank.openpipe.solr.util.TokenFilterFactoryFactory
 * @version $Revision$
 */
public class SolrAnalyzerStep extends MultiInputFieldPipelineStep {
   private TokenizerFactory tokenizerFactory;
   private List<TokenFilterFactory> filterFactories = Collections.emptyList();
   @NotNull
   private Set<String> annotations = Collections.emptySet();

   @Override
   protected void process(Document doc, String fieldName, List<AnnotatedField> fieldValues) throws PipelineException {
      try {
         for (AnnotatedField value : fieldValues) {
            processFilters(value);
         }
      } catch (IOException e) {
         throw new PipelineException(e);
      }
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
      super.prepare();
      
      if (tokenizerFactory == null && filterFactories.isEmpty()) {
         throw new PipelineException("Either tokenizerFactory or filterFactories must be provided");
      }
   }

   @Override
   public String getRevision() {
      return "$Revision$";
   }

   /**
    * Gets the SolR TokenizerFactory that is used.
    *
    * @return the TokenizerFactory
    */
   public TokenizerFactory getTokenizerFactory() {
      return tokenizerFactory;
   }

   /**
    * Sets the SolR TokenizerFactory to use.
    *
    * @param tokenizerFactory a TokenizerFactory
    */
   public void setTokenizerFactory(TokenizerFactory tokenizerFactory) {
      this.tokenizerFactory = tokenizerFactory;
   }

   /**
    * Gets the list of SolR <tt>TokenFilterFactories</tt> that is used.
    *
    * @see #setTokenizerFactory(org.apache.solr.analysis.TokenizerFactory)
    * @return a list of TokenFilterFactory objects
    */
   public List<TokenFilterFactory> getFilterFactories() {
      return filterFactories;
   }

   /**
    * Sets the list of initialized SolR <tt>TokenFilterFactories</tt> to use for analasys.
    *
    * @param filterFactories the filterFactories to use
    */
   public void setFilterFactories(List<TokenFilterFactory> filterFactories) {
      this.filterFactories = filterFactories;
   }

   /**
    * Gets the set of annotation types to treat as tokens. Used only if <tt>TokenizerFactory</tt> is not set. 
    *
    * @return a set of Strings
    */
   public Set<String> getAnnotations() {
      return annotations;
   }

   /**
    * Sets the set of annotation types to treat as tokens. Used only if <tt>TokenizerFactory</tt> is not set.
    *
    * @param annotations a set of Strings
    */
   public void setAnnotations(Set<String> annotations) {
      this.annotations = annotations;
   }
}
