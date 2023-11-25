package org.matsim.application.options;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import picocli.CommandLine;

/** Common options when working with counts data. */
public final class CountsOption {

  @CommandLine.Option(
      names = "--ignored-counts",
      description = "path to csv with count station ids to ignore")
  private Path ignored;

  @CommandLine.Option(
      names = "--manual-matched-counts",
      description = "path to csv with manual matched count stations and link ids")
  private Path manual;

  private Set<String> ignoredCounts = null;

  private Map<String, Id<Link>> manualMatchedCounts = null;

  public CountsOption() {}

  public CountsOption(@Nullable Path ignored, @Nullable Path manual) {
    this.ignored = ignored;
    this.manual = manual;
  }

  /** Get list of ignored count ids. */
  public Set<String> getIgnored() {
    readIgnored();
    return ignoredCounts;
  }

  /** Return mapping of count id to specified link id. */
  public Map<String, Id<Link>> getManualMatched() {
    readManualMatched();
    return manualMatchedCounts;
  }

  private void readManualMatched() {

    // Already read
    if (manualMatchedCounts != null) return;

    if (manual == null) {
      manualMatchedCounts = new HashMap<>();
      return;
    }

    try (var reader = Files.newBufferedReader(manual)) {
      List<CSVRecord> records =
          CSVFormat.Builder.create()
              .setAllowMissingColumnNames(true)
              .setDelimiter(';')
              .build()
              .parse(reader)
              .getRecords();

      manualMatchedCounts =
          records.stream().collect(Collectors.toMap(r -> r.get(0), r -> Id.createLinkId(r.get(1))));

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void readIgnored() {
    // Already read the counts
    if (ignoredCounts != null) return;

    if (ignored == null) {
      ignoredCounts = new HashSet<>();
      return;
    }

    try {
      ignoredCounts = new HashSet<>(Files.readAllLines(ignored));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Check whether station id should be ignored. */
  public boolean isIgnored(String stationId) {
    readIgnored();
    return ignoredCounts.contains(stationId);
  }

  /**
   * Return manually matched link id.
   *
   * @return null if not matched
   */
  public Id<Link> isManuallyMatched(String stationId) {
    readManualMatched();

    if (manualMatchedCounts.isEmpty() || !manualMatchedCounts.containsKey(stationId)) return null;

    return manualMatchedCounts.get(stationId);
  }
}
