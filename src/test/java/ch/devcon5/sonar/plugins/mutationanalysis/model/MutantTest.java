/*
 * Mutation Analysis Plugin
 * Copyright (C) 2015-2018 DevCon5 GmbH, Switzerland
 * info@devcon5.ch
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ch.devcon5.sonar.plugins.mutationanalysis.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MutantTest {

  private Mutant detectedMutant;
  private Mutant undetectedMutant;

  public static Mutant newUndetectedMutant() {

    return Mutant.builder()
                 .mutantStatus(Mutant.State.NO_COVERAGE)
                 .inSourceFile("SomeClass.java")
                 .inClass("com.foo.bar.SomeClass")
                 .inMethod("anyMethod")
                 .withMethodParameters("anyMethodDesc")
                 .inLine(8)
                 .usingMutator(MutationOperators.find("INVERT_NEGS"))
                 .atIndex(10)
                 .killedBy("com.foo.bar.SomeClassKillingTest")
                 .build();
  }

  public static Mutant newDetectedMutant() {

    return Mutant.builder()
                 .mutantStatus(Mutant.State.KILLED)
                 .inSourceFile("SomeClass.java")
                 .inClass("com.foo.bar.SomeClass")
                 .inMethod("anyMethod")
                 .withMethodParameters("anyMethodDesc")
                 .inLine(17)
                 .usingMutator(MutationOperators.find("INVERT_NEGS"))
                 .atIndex(5)
                 .killedBy("com.foo.bar.SomeClassKillingTest")
                 .build();
  }

  @Before
  public void setUp() throws Exception {

    detectedMutant = newDetectedMutant();
    undetectedMutant = newUndetectedMutant();
  }

  @Test
  public void testIsDetected_true() throws Exception {

    assertTrue(detectedMutant.isDetected());
  }

  @Test
  public void testIsDetected_false() throws Exception {

    assertFalse(undetectedMutant.isDetected());
  }

  @Test
  public void testGetMutantStatus_killed() throws Exception {

    assertEquals(Mutant.State.KILLED, detectedMutant.getState());
  }

  @Test
  public void testGetMutantStatus_noCoverage() throws Exception {

    assertEquals(Mutant.State.NO_COVERAGE, undetectedMutant.getState());
  }

  @Test
  public void testGetSourceFile() throws Exception {

    assertEquals("SomeClass.java", detectedMutant.getSourceFile());
  }

  @Test
  public void testGetMutatedClass() throws Exception {

    assertEquals("com.foo.bar.SomeClass", detectedMutant.getMutatedClass());
  }

  @Test
  public void testGetMutatedMethod() throws Exception {

    assertEquals("anyMethod", detectedMutant.getMutatedMethod());
  }

  @Test
  public void testGetMethodDescription() throws Exception {

    assertEquals("anyMethodDesc", detectedMutant.getMethodDescription());
  }

  @Test
  public void testGetLineNumber() throws Exception {

    assertEquals(17, detectedMutant.getLineNumber());
    assertEquals(8, undetectedMutant.getLineNumber());
  }

  @Test
  public void testGetMutator() throws Exception {

    final MutationOperator mutationOperator = MutationOperators.find("org.pitest.mutationtest.engine.gregor.mutators.InvertNegsMutator");
    assertNotNull(mutationOperator);
    assertEquals(mutationOperator, detectedMutant.getMutationOperator());
  }

  @Test
  public void testGetMutatorSuffix_nonEmptySuffix() throws Exception {

    assertEquals("", detectedMutant.getMutatorSuffix());

  }

  @Test
  public void testGetMutatorSuffix_emptySuffix() throws Exception {

    Mutant mutant = Mutant.builder()
                          .mutantStatus(Mutant.State.NO_COVERAGE)
                          .inSourceFile("SomeClass.java")
                          .inClass("com.foo.bar.SomeClass")
                          .inMethod("anyMethod")
                          .withMethodParameters("anyMethodDesc")
                          .inLine(8)
                          .usingMutator("org.pitest.mutationtest.engine.gregor.mutators.RemoveConditionalMutator_EQUAL_ELSE")
                          .atIndex(10)
                          .killedBy("com.foo.bar.SomeClassKillingTest")
                          .build();

    assertEquals("EQUAL_ELSE", mutant.getMutatorSuffix());

  }

  @Test
  public void testGetIndex() throws Exception {

    assertEquals(5, detectedMutant.getIndex());
    assertEquals(10, undetectedMutant.getIndex());
  }

  @Test
  public void testGetKillingTest() throws Exception {

    assertEquals("com.foo.bar.SomeClassKillingTest", detectedMutant.getKillingTest());
  }

  @Test
  public void testToString() throws Exception {

    assertEquals("Mutant [sourceFile=SomeClass.java, "
                     + "mutatedClass=com.foo.bar.SomeClass, "
                     + "mutatedMethod=anyMethod, "
                     + "methodDescription=anyMethodDesc, "
                     + "lineNumber=17, "
                     + "state=KILLED, "
                     + "mutationOperator=Invert Negs Mutator, "
                     + "killingTest=com.foo.bar.SomeClassKillingTest]", detectedMutant.toString());

  }

  @Test
  public void testEquals_same_true() throws Exception {

    assertEquals(detectedMutant, detectedMutant);
  }

  @Test
  public void testEquals_twin_true() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant twin = newDetectedMutant();
    assertEquals(expected, twin);
  }

  @Test
  public void testEquals_differentDetected_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = newUndetectedMutant();

    assertNotEquals(expected, other);
  }

  @Test
  public void testEquals_differentStatus_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(Mutant.State.UNKNOWN)
                               .inSourceFile(expected.getSourceFile())
                               .inClass(expected.getMutatedClass())
                               .inMethod(expected.getMutatedMethod())
                               .inLine(expected.getLineNumber())
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();

    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentSourceFile_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile("other")
                               .inClass(expected.getMutatedClass())
                               .inMethod(expected.getMutatedMethod())
                               .inLine(expected.getLineNumber())
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();
    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentMutatedClass_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile(expected.getSourceFile())
                               .inClass("com.otherClass")
                               .inMethod(expected.getMutatedMethod())
                               .inLine(expected.getLineNumber())
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();

    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentMutatedMethod_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile(expected.getSourceFile())
                               .inClass(expected.getMutatedClass())
                               .inMethod("otherMethod")
                               .inLine(expected.getLineNumber())
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();
    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentMethodDescription_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile(expected.getSourceFile())
                               .inClass(expected.getMutatedClass())
                               .inMethod(expected.getMutatedMethod())
                               .inLine(expected.getLineNumber())
                               .withMethodParameters("()")
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();

    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentLineNumber_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile(expected.getSourceFile())
                               .inClass(expected.getMutatedClass())
                               .inMethod(expected.getMutatedMethod())
                               .inLine(127)
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();

    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentMutator_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile(expected.getSourceFile())
                               .inClass(expected.getMutatedClass())
                               .inMethod(expected.getMutatedMethod())
                               .inLine(expected.getLineNumber())
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(MutationOperators.find("ARGUMENT_PROPAGATION"))
                               .atIndex(expected.getIndex())
                               .killedBy(expected.getKillingTest())
                               .build();

    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentIndex_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant other = Mutant.builder()
                               .mutantStatus(expected.getState())
                               .inSourceFile(expected.getSourceFile())
                               .inClass(expected.getMutatedClass())
                               .inMethod(expected.getMutatedMethod())
                               .inLine(expected.getLineNumber())
                               .withMethodParameters(expected.getMethodDescription())
                               .usingMutator(expected.getMutationOperator())
                               .atIndex(127)
                               .killedBy(expected.getKillingTest())
                               .build();

    assertNotEquals(detectedMutant, other);
  }

  @Test
  public void testEquals_differentKillingTest_false() throws Exception {

    final Mutant expected = detectedMutant;
    final Mutant twin = Mutant.builder()
                              .mutantStatus(expected.getState())
                              .inSourceFile(expected.getSourceFile())
                              .inClass(expected.getMutatedClass())
                              .inMethod(expected.getMutatedMethod())
                              .inLine(expected.getLineNumber())
                              .withMethodParameters(expected.getMethodDescription())
                              .usingMutator(expected.getMutationOperator())
                              .atIndex(expected.getIndex())
                              .killedBy("otherTest")
                              .build();

    assertNotEquals(detectedMutant, twin);
  }

  @Test
  public void testEquals_null_false() throws Exception {

    assertNotEquals(detectedMutant, null);
  }

  @Test
  public void testEquals_otherObject_false() throws Exception {

    assertNotEquals(detectedMutant, new Object());
  }

  @Test
  public void testHashCode_detected_reproducible() throws Exception {

    // for the same object we always have the same hashCode
    final int prime = 31;
    int refCode = 1;
    refCode = prime * refCode + detectedMutant.getIndex();
    refCode = prime * refCode + 1231;
    refCode = prime * refCode + detectedMutant.getLineNumber();
    refCode = prime * refCode + detectedMutant.getMethodDescription().hashCode();
    refCode = prime * refCode + detectedMutant.getState().hashCode();
    refCode = prime * refCode + detectedMutant.getMutatedClass().hashCode();
    refCode = prime * refCode + detectedMutant.getMutatedMethod().hashCode();
    refCode = prime * refCode + detectedMutant.getMutationOperator().hashCode();
    refCode = prime * refCode + detectedMutant.getMutatorSuffix().hashCode();
    refCode = prime * refCode + detectedMutant.getSourceFile().hashCode();
    refCode = prime * refCode + detectedMutant.getKillingTest().hashCode();

    assertEquals(refCode, detectedMutant.hashCode());
  }

  @Test
  public void testHashCode_undetected_reproducible() throws Exception {

    // for the same object we always have the same hashCode
    final int prime = 31;
    int refCode = 1;
    refCode = prime * refCode + undetectedMutant.getIndex();
    refCode = prime * refCode + 1237;
    refCode = prime * refCode + undetectedMutant.getLineNumber();
    refCode = prime * refCode + undetectedMutant.getMethodDescription().hashCode();
    refCode = prime * refCode + undetectedMutant.getState().hashCode();
    refCode = prime * refCode + undetectedMutant.getMutatedClass().hashCode();
    refCode = prime * refCode + undetectedMutant.getMutatedMethod().hashCode();
    refCode = prime * refCode + undetectedMutant.getMutationOperator().hashCode();
    refCode = prime * refCode + undetectedMutant.getMutatorSuffix().hashCode();
    refCode = prime * refCode + undetectedMutant.getSourceFile().hashCode();
    refCode = prime * refCode + undetectedMutant.getKillingTest().hashCode();

    assertEquals(refCode, undetectedMutant.hashCode());
  }

  @Test
  public void testHashCode_sameMutant() throws Exception {

    assertEquals(detectedMutant.hashCode(), detectedMutant.hashCode());
  }

  @Test
  public void testHashCode_otherMutantObject() throws Exception {

    assertNotEquals(detectedMutant.hashCode(), undetectedMutant.hashCode());
  }

  @Test
  public void testBuilder() throws Exception {

    final Mutant.Builder builder = Mutant.builder();
    assertNotNull(builder);
  }

}
