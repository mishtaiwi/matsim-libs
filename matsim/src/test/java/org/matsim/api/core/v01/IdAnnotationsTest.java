package org.matsim.api.core.v01;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Test;
import org.matsim.api.core.v01.IdAnnotations.JsonId;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;

public class IdAnnotationsTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testRecordJsonIds() throws JsonProcessingException {
    Id<Person> personId = Id.createPersonId("person");
    RecordWithIds recordWithIds1 =
        new RecordWithIds(personId, Id.createLinkId("link"), Id.createNodeId("node"));

    String s = objectMapper.writeValueAsString(recordWithIds1);
    RecordWithIds recordWithIds2 = objectMapper.readValue(s, RecordWithIds.class);

    Assert.assertEquals(recordWithIds1, recordWithIds2);
    Assert.assertEquals(personId, recordWithIds2.personId);
    Assert.assertSame(personId, recordWithIds2.personId);
  }

  @Test
  public void testRecordJsonIdsWithNull() throws JsonProcessingException {
    Id<Person> personId = null;
    RecordWithIds recordWithIds1 = new RecordWithIds(personId, null, null);

    String s = objectMapper.writeValueAsString(recordWithIds1);
    RecordWithIds recordWithIds2 = objectMapper.readValue(s, RecordWithIds.class);

    Assert.assertEquals(recordWithIds1, recordWithIds2);
    Assert.assertEquals(personId, recordWithIds2.personId);
    Assert.assertSame(personId, recordWithIds2.personId);
  }

  @Test
  public void testClassJsonIds() throws JsonProcessingException {
    Id<Person> personId = Id.createPersonId("person");
    ClassWithIds classWithIds1 =
        new ClassWithIds(personId, Id.createLinkId("link"), Id.createNodeId("node"));

    String s = objectMapper.writeValueAsString(classWithIds1);
    ClassWithIds classWithIds2 = objectMapper.readValue(s, ClassWithIds.class);

    Assert.assertEquals(classWithIds1, classWithIds2);
    Assert.assertEquals(personId, classWithIds2.personId);
    Assert.assertSame(personId, classWithIds2.personId);
  }

  @Test
  public void testClassJsonIdsWithNull() throws JsonProcessingException {
    Id<Person> personId = null;
    ClassWithIds classWithIds1 = new ClassWithIds(personId, null, null);

    String s = objectMapper.writeValueAsString(classWithIds1);
    ClassWithIds classWithIds2 = objectMapper.readValue(s, ClassWithIds.class);

    Assert.assertEquals(classWithIds1, classWithIds2);
    Assert.assertEquals(personId, classWithIds2.personId);
    Assert.assertSame(personId, classWithIds2.personId);
  }

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  private static record RecordWithIds(
      @JsonId Id<Person> personId, @JsonId Id<Link> linkId, @JsonId Id<Node> nodeId) {}
  ;

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  private static class ClassWithIds {

    @JsonId Id<Person> personId;

    @JsonId Id<Link> linkId;

    @JsonId Id<Node> nodeId;

    ClassWithIds() {
      // for deserialization
    }

    ClassWithIds(Id<Person> personId, Id<Link> linkId, Id<Node> nodeId) {
      this.personId = personId;
      this.linkId = linkId;
      this.nodeId = nodeId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      } else if (o instanceof ClassWithIds classWithIds) {
        return Objects.equals(personId, classWithIds.personId)
            && Objects.equals(linkId, classWithIds.linkId)
            && Objects.equals(nodeId, classWithIds.nodeId);
      }
      return false;
    }
  }
  ;
}
