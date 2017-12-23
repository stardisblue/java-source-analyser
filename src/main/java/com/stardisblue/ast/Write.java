package com.stardisblue.ast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
 */
public class Write {
    public static void json(String fileName, List<String> nodes, List<String> links) throws IOException {
        File file = new File(fileName);
        String data = "{\"nodes\":[" + String.join(",", nodes) + "], " +
                "\"links\":[" + String.join(",", links) + "]}";
        FileUtils.write(file, data, UTF_8);
    }
}
