package ai.preferred.crawler;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityCSVReader<T> implements Iterator<T>, AutoCloseable {

  private static final Logger LOGGER = LoggerFactory.getLogger(EntityCSVReader.class);

  private final Class<T> clazz;

  private final CSVParser csvParser;

  private final CopyOnWriteArrayList<String> headers;

  private final Iterator<CSVRecord> csvRecordIterator;

  public EntityCSVReader(final String file, final Class<T> clazz) throws IOException {
    this(file, clazz, CSVFormat.DEFAULT);
  }

  public EntityCSVReader(final String file, final Class<T> clazz, final CSVFormat csvFormat) throws IOException {
    final Reader reader = Files.newBufferedReader(Paths.get(file));
    this.clazz = clazz;
    this.csvParser = csvFormat.withFirstRecordAsHeader().parse(reader);
    this.headers = new CopyOnWriteArrayList<>(getHeaderList(clazz));
    this.csvRecordIterator = csvParser.iterator();
  }

  private static List<String> getHeaderList(final Class<?> clazz) {
    final List<String> result = new ArrayList<>();
    for (final Field field : clazz.getDeclaredFields()) {
      result.add(field.getName());
    }
    return result;
  }

  @Override
  public boolean hasNext() {
    return csvRecordIterator.hasNext();
  }

  @Override
  public T next() {
    try {
      final T row = clazz.getDeclaredConstructor((Class<?>[]) null).newInstance();
      final CSVRecord record = csvRecordIterator.next();

      for (String header : headers) {
        final Field declaredField = clazz.getDeclaredField(header);
        final boolean accessible = declaredField.canAccess(row);
        declaredField.setAccessible(true);
        try {
          declaredField.set(row, record.get(header));
        } catch (IllegalArgumentException e) {
          LOGGER.warn("Column name \"{}\" is not found, this field will not be populated.", header);
          headers.remove(header);
        }
        declaredField.setAccessible(accessible);
      }
      return row;
    } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | InvocationTargetException
        | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws IOException {
    csvParser.close();
  }
}
