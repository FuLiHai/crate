/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.analyze;


import io.crate.exceptions.TableUnknownException;
import io.crate.metadata.Schemas;
import io.crate.metadata.TableIdent;
import io.crate.metadata.blob.BlobSchemaInfo;
import io.crate.test.integration.CrateUnitTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DropBlobTableAnalyzedStatementTest extends CrateUnitTest {

    public static final String IRRELEVANT = "Irrelevant";

    private Schemas schemas;

    private DropBlobTableAnalyzedStatement dropBlobTableAnalyzedStatement;


    @Before
    public void prepare() {
        schemas = mock(Schemas.class);
    }

    @Test
    public void testDeletingNoExistingTableSetsNoopIfIgnoreNonExistentTablesIsSet() throws Exception {
        TableIdent tableIdent = new TableIdent(BlobSchemaInfo.NAME, IRRELEVANT);
        when(schemas.getWritableTable(tableIdent)).thenThrow(new TableUnknownException(tableIdent));

        dropBlobTableAnalyzedStatement = new DropBlobTableAnalyzedStatement(schemas, true);
        dropBlobTableAnalyzedStatement.table(tableIdent);
        assertThat(dropBlobTableAnalyzedStatement.noop(), is(true));
    }

    @Test
    public void testDeletingNonExistingTableRaisesException() throws Exception {
        expectedException.expect(TableUnknownException.class);
        expectedException.expectMessage("Table 'blob.Irrelevant' unknown");
        TableIdent tableIdent = new TableIdent(BlobSchemaInfo.NAME, IRRELEVANT);
        when(schemas.getWritableTable(tableIdent)).thenThrow(new TableUnknownException(tableIdent));

        dropBlobTableAnalyzedStatement = new DropBlobTableAnalyzedStatement(schemas, false);
        dropBlobTableAnalyzedStatement.table(tableIdent);
    }
}