# JExpected - Expected Error Handling in Java

## Usage

``` Java
package main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.function.Function;

import util.function.Expected;

public class Demo {
    enum URLError {
        MALFORMED_URL, IO_ERROR
    }

    /**
     * Opens and read the provided URL
     *
     * @param urlString
     *            the URL as String
     * @return an Expected with the content as String, in case of an error the
     *         corresponding URLError will be returned
     */
    public static Expected<String, URLError> readURL(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
            URLConnection urlConnection = url.openConnection();
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null)
                    sb.append(line);
                return Expected.expected(new String(sb.toString()));
            }
        } catch (MalformedURLException e) {
            return Expected.unexpected(URLError.MALFORMED_URL);
        } catch (IOException e) {
            return Expected.unexpected(URLError.IO_ERROR);
        }
    }

    public void someFunc() {
        // Functional programming using 'ifValue', 'map' and 'bind' to process
        // expected values
        final String URLs[] = { "http://google.com", "http://bing.com", "http://yahoo.com" };
        Arrays.asList(URLs).parallelStream().map(Demo::readURL).forEach(e -> e.ifValue(System.out::println));

        // Error handling using the expected
        Expected<String, URLError> expected = readURL("<?!>");
        if (expected.hasErrorValue()) {
            URLError error = expected.getErrorValue();
            // TODO cope with error
        }
    }
}


```

