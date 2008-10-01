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
package no.trank.openpipe.wikipedia.producer.meta;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

/**
 * @version $Revision$
 */
public class RssMetaParser {
   private static Pattern PATT_RSS = Pattern.compile(
           "<channel>\\s*<title>(.+)</title>\\s*<link>(.+/(\\w+)/(\\d+))</link>.*" +
                   "<item>.*<pubDate>(.+)</pubDate>\\s*</item>\\s*</channel>", Pattern.DOTALL);
   private final URL fileUrl;
   private final URL md5Url;
   private final Date modifiedDate;
   private String fileName;

   public RssMetaParser(String rss) throws MalformedURLException {
      final Matcher matcher = PATT_RSS.matcher(rss);
      if (!matcher.find()) {
         throw new IllegalArgumentException("input does not match pattern '" + PATT_RSS.pattern() + "' was: [" +
                 rss + ']');
      }
      final URL baseUrl = new URL(matcher.group(2) + '/');
      final String filePrefix = matcher.group(3) + '-' + matcher.group(4);
      fileName = filePrefix + '-' + matcher.group(1);
      fileUrl = new URL(baseUrl, fileName);
      md5Url = new URL(baseUrl, filePrefix + "-md5sums.txt");
      try {
         modifiedDate = DateUtil.parseDate(matcher.group(5));
      } catch (DateParseException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public URL getFileUrl() {
      return fileUrl;
   }

   public URL getMd5Url() {
      return md5Url;
   }

   public String findMd5(String md5sums) {
      if (md5sums != null) {
         final Matcher matcher = Pattern.compile("^([0-9a-fA-F]{32})\\s+" + fileName + '$', Pattern.MULTILINE).matcher(md5sums);
         if (matcher.find()) {
            return matcher.group(1);
         }
      }
      return null;
   }

   public Date getModifiedDate() {
      return modifiedDate;
   }
}