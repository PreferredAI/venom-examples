package ai.preferred.crawler.iproperty.csv;

import ai.preferred.crawler.iproperty.entity.Property;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.output.FileWriterWithEncoding;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PropertyStorage implements Closeable {

  private final CSVPrinter printer;

  public PropertyStorage(String filename) throws IOException {
    printer = new CSVPrinter(new FileWriterWithEncoding(filename, StandardCharsets.UTF_8), CSVFormat.EXCEL);
  }

  public synchronized void append(List<Object> header) throws IOException {
    printer.printRecord(header);
  }

  public synchronized void append(Property property) throws IOException {
    printer.printRecord(property.asList());
  }

  @Override
  public void close() throws IOException {
    printer.close(true);
  }

}
