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
package no.trank.openpipe.parse.api;

import java.io.IOException;

/**
 * An interface representing a parser.
 * 
 * @version $Revision$
 */
public interface Parser {

   /**
    * Parse the given data.
    * 
    * @param data the data to be parsed.
    * 
    * @return the result of the parse. <b>Note</b> must <i>not</i> return <tt>null</tt>.
    * 
    * @throws IOException if an I/O-error occured.
    * @throws ParserException if the data could not be parsed.
    */
   ParserResult parse(ParseData data) throws IOException, ParserException;
}
