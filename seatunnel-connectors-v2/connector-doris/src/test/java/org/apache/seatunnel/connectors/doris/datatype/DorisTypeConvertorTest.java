/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.connectors.doris.datatype;

import org.apache.seatunnel.api.table.catalog.Column;
import org.apache.seatunnel.api.table.catalog.PhysicalColumn;
import org.apache.seatunnel.api.table.converter.BasicTypeDefine;
import org.apache.seatunnel.api.table.type.ArrayType;
import org.apache.seatunnel.api.table.type.BasicType;
import org.apache.seatunnel.api.table.type.DecimalType;
import org.apache.seatunnel.api.table.type.LocalTimeType;
import org.apache.seatunnel.api.table.type.PrimitiveByteArrayType;
import org.apache.seatunnel.common.exception.SeaTunnelRuntimeException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mysql.cj.MysqlType;

import java.util.Locale;

public class DorisTypeConvertorTest {

    @Test
    public void testConvertUnsupported() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder().name("test").columnType("aaa").dataType("aaa").build();
        try {
            DorisTypeConverter.INSTANCE.convert(typeDefine);
            Assertions.fail();
        } catch (SeaTunnelRuntimeException e) {
            // ignore
        } catch (Throwable e) {
            Assertions.fail();
        }
    }

    @Test
    public void testConvertNull() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("null")
                        .dataType("null")
                        .nullable(true)
                        .defaultValue("null")
                        .comment("null")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.VOID_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
        Assertions.assertEquals(typeDefine.isNullable(), column.isNullable());
        Assertions.assertEquals(typeDefine.getDefaultValue(), column.getDefaultValue());
        Assertions.assertEquals(typeDefine.getComment(), column.getComment());
    }

    @Test
    public void testConvertTinyint() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("tinyint(1)")
                        .dataType("tinyint")
                        .length(1L)
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.BOOLEAN_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("tinyint(2)")
                        .dataType("tinyint")
                        .length(2L)
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.SHORT_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("tinyint")
                        .dataType("tinyint")
                        .unsigned(false)
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.SHORT_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertSmallint() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("smallint")
                        .dataType("smallint")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.SHORT_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertInt() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder().name("test").columnType("int").dataType("int").build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.INT_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertBoolean() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("tinyint(1)")
                        .dataType("tinyint")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.BOOLEAN_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertBigint() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("bigint")
                        .dataType("bigint")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.LONG_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertLargeint() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("largeint")
                        .dataType("bigint unsigned")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.STRING_TYPE, column.getDataType());
        Assertions.assertEquals(39, column.getColumnLength());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertFloat() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("float")
                        .dataType("float")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.FLOAT_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertDouble() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("double")
                        .dataType("double")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.DOUBLE_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertDecimal() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("decimal(18,9)")
                        .dataType("decimal")
                        .precision(27L)
                        .scale(9)
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(new DecimalType(27, 9), column.getDataType());
        Assertions.assertEquals(27, column.getColumnLength());
        Assertions.assertEquals(9, column.getScale());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("decimalv3")
                        .dataType("decimal")
                        .precision(9L)
                        .scale(2)
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(
                new DecimalType(DorisTypeConverter.DEFAULT_PRECISION, 2), column.getDataType());
        Assertions.assertEquals(9L, column.getColumnLength());
        Assertions.assertEquals(2, column.getScale());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("decimalv3(36,2)")
                        .dataType("decimal")
                        .precision(38L)
                        .scale(2)
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(new DecimalType(38, 2), column.getDataType());
        Assertions.assertEquals(38L, column.getColumnLength());
        Assertions.assertEquals(2, column.getScale());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertChar() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("char(2)")
                        .dataType("char")
                        .length(2L)
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.STRING_TYPE, column.getDataType());
        Assertions.assertEquals(2, column.getColumnLength());
        Assertions.assertEquals(
                typeDefine.getColumnType(), column.getSourceType().toLowerCase(Locale.ROOT));

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("varchar(2)")
                        .dataType("varchar")
                        .length(2L)
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.STRING_TYPE, column.getDataType());
        Assertions.assertEquals(2, column.getColumnLength());
        Assertions.assertEquals(
                typeDefine.getColumnType(), column.getSourceType().toLowerCase(Locale.ROOT));
    }

    @Test
    public void testConvertString() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("string")
                        .dataType("varchar")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.STRING_TYPE, column.getDataType());
        Assertions.assertEquals(DorisTypeConverter.MAX_STRING_LENGTH, column.getColumnLength());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertJson() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder().name("test").columnType("json").dataType("json").build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.STRING_TYPE, column.getDataType());
        Assertions.assertEquals(DorisTypeConverter.MAX_STRING_LENGTH, column.getColumnLength());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("jsonb")
                        .dataType("jsonb")
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(BasicType.STRING_TYPE, column.getDataType());
        Assertions.assertEquals(DorisTypeConverter.MAX_STRING_LENGTH, column.getColumnLength());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertDate() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder().name("test").columnType("date").dataType("date").build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(LocalTimeType.LOCAL_DATE_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertDateV2() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("datev2")
                        .dataType("date")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(LocalTimeType.LOCAL_DATE_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testConvertDatetime() {
        BasicTypeDefine<Object> typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("datetime")
                        .dataType("datetime")
                        .build();
        Column column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(LocalTimeType.LOCAL_DATE_TIME_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());

        typeDefine =
                BasicTypeDefine.builder()
                        .name("test")
                        .columnType("datetimev2(3)")
                        .dataType("datetime")
                        .scale(3)
                        .build();
        column = DorisTypeConverter.INSTANCE.convert(typeDefine);
        Assertions.assertEquals(typeDefine.getName(), column.getName());
        Assertions.assertEquals(LocalTimeType.LOCAL_DATE_TIME_TYPE, column.getDataType());
        Assertions.assertEquals(typeDefine.getScale(), column.getScale());
        Assertions.assertEquals(typeDefine.getColumnType(), column.getSourceType());
    }

    @Test
    public void testStringTooLong() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(4294967295L)
                        .build();
        try {
            DorisTypeConverter.INSTANCE.reconvert(column);
            Assertions.fail();
        } catch (SeaTunnelRuntimeException e) {
            // ignore
        } catch (Throwable e) {
            Assertions.fail();
        }
    }

    @Test
    public void testReconvertNull() {
        Column column =
                PhysicalColumn.of("test", BasicType.VOID_TYPE, (Long) null, true, "null", "null");

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_NULL, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_NULL, typeDefine.getDataType());
        Assertions.assertEquals(column.isNullable(), typeDefine.isNullable());
        Assertions.assertEquals(column.getDefaultValue(), typeDefine.getDefaultValue());
        Assertions.assertEquals(column.getComment(), typeDefine.getComment());
    }

    @Test
    public void testReconvertBoolean() {
        Column column =
                PhysicalColumn.builder().name("test").dataType(BasicType.BOOLEAN_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BOOLEAN, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BOOLEAN, typeDefine.getDataType());
        Assertions.assertEquals(1, typeDefine.getLength());
    }

    @Test
    public void testReconvertByte() {
        Column column = PhysicalColumn.builder().name("test").dataType(BasicType.BYTE_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_TINYINT, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_TINYINT, typeDefine.getDataType());
    }

    @Test
    public void testReconvertShort() {
        Column column =
                PhysicalColumn.builder().name("test").dataType(BasicType.SHORT_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_SMALLINT, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_SMALLINT, typeDefine.getDataType());
    }

    @Test
    public void testReconvertInt() {
        Column column = PhysicalColumn.builder().name("test").dataType(BasicType.INT_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_INT, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_INT, typeDefine.getDataType());
    }

    @Test
    public void testReconvertLong() {
        Column column = PhysicalColumn.builder().name("test").dataType(BasicType.LONG_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BIGINT, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BIGINT, typeDefine.getDataType());
    }

    @Test
    public void testReconvertFloat() {
        Column column =
                PhysicalColumn.builder().name("test").dataType(BasicType.FLOAT_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_FLOAT, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_FLOAT, typeDefine.getDataType());
    }

    @Test
    public void testReconvertDouble() {
        Column column =
                PhysicalColumn.builder().name("test").dataType(BasicType.DOUBLE_TYPE).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DOUBLE, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DOUBLE, typeDefine.getDataType());
    }

    @Test
    public void testReconvertDecimal() {
        Column column =
                PhysicalColumn.builder().name("test").dataType(new DecimalType(0, 0)).build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                String.format(
                        "%s(%s,%s)",
                        DorisTypeConverter.DORIS_DECIMALV3,
                        DorisTypeConverter.DEFAULT_PRECISION,
                        DorisTypeConverter.DEFAULT_SCALE),
                typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DECIMALV3, typeDefine.getDataType());

        column = PhysicalColumn.builder().name("test").dataType(new DecimalType(10, 2)).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DECIMALV3, typeDefine.getDataType());
        Assertions.assertEquals(
                String.format("%s(%s,%s)", DorisTypeConverter.DORIS_DECIMALV3, 10, 2),
                typeDefine.getColumnType());
    }

    @Test
    public void testReconvertBytes() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(PrimitiveByteArrayType.INSTANCE)
                        .columnLength(null)
                        .build();

        BasicTypeDefine typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(PrimitiveByteArrayType.INSTANCE)
                        .columnLength(255L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(PrimitiveByteArrayType.INSTANCE)
                        .columnLength(65535L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(PrimitiveByteArrayType.INSTANCE)
                        .columnLength(16777215L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(PrimitiveByteArrayType.INSTANCE)
                        .columnLength(4294967295L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getDataType());
    }

    @Test
    public void testReconvertString() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(null)
                        .sourceType(DorisTypeConverter.DORIS_LARGEINT)
                        .build();

        BasicTypeDefine typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_LARGEINT, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_LARGEINT, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(null)
                        .sourceType(DorisTypeConverter.DORIS_JSON)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_JSONB, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_JSONB, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(null)
                        .sourceType(DorisTypeConverter.DORIS_JSONB)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_JSONB, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_JSONB, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(255L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                String.format("%s(%s)", DorisTypeConverter.DORIS_CHAR, column.getColumnLength()),
                typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_CHAR, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(65535L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                String.format("%s(%s)", DorisTypeConverter.DORIS_VARCHAR, column.getColumnLength()),
                typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_VARCHAR, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(BasicType.STRING_TYPE)
                        .columnLength(16777215L)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING, typeDefine.getDataType());
    }

    @Test
    public void testReconvertDate() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(LocalTimeType.LOCAL_DATE_TYPE)
                        .build();

        BasicTypeDefine typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DATEV2, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DATEV2, typeDefine.getDataType());
    }

    @Test
    public void testReconvertTime() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(LocalTimeType.LOCAL_TIME_TYPE)
                        .build();

        BasicTypeDefine typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                String.format("%s(%s)", DorisTypeConverter.DORIS_VARCHAR, 8),
                typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_VARCHAR, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(LocalTimeType.LOCAL_TIME_TYPE)
                        .scale(3)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                String.format("%s(%s)", DorisTypeConverter.DORIS_VARCHAR, 8),
                typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_VARCHAR, typeDefine.getDataType());
    }

    @Test
    public void testReconvertDatetime() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(LocalTimeType.LOCAL_DATE_TIME_TYPE)
                        .build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DATETIMEV2, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DATETIMEV2, typeDefine.getDataType());

        column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(LocalTimeType.LOCAL_DATE_TIME_TYPE)
                        .scale(3)
                        .build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                String.format("%s(%s)", DorisTypeConverter.DORIS_DATETIMEV2, column.getScale()),
                typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DATETIMEV2, typeDefine.getDataType());
        Assertions.assertEquals(column.getScale(), typeDefine.getScale());
    }

    @Test
    public void testReconvertArray() {
        Column column =
                PhysicalColumn.builder()
                        .name("test")
                        .dataType(ArrayType.BOOLEAN_ARRAY_TYPE)
                        .build();

        BasicTypeDefine<MysqlType> typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BOOLEAN_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BOOLEAN_ARRAY, typeDefine.getDataType());

        column = PhysicalColumn.builder().name("test").dataType(ArrayType.BYTE_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_TINYINT_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_TINYINT_ARRAY, typeDefine.getDataType());

        column =
                PhysicalColumn.builder().name("test").dataType(ArrayType.STRING_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_STRING_ARRAY, typeDefine.getDataType());

        column = PhysicalColumn.builder().name("test").dataType(ArrayType.SHORT_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(
                DorisTypeConverter.DORIS_SMALLINT_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_SMALLINT_ARRAY, typeDefine.getDataType());

        column = PhysicalColumn.builder().name("test").dataType(ArrayType.INT_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_INT_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_INT_ARRAY, typeDefine.getDataType());

        column = PhysicalColumn.builder().name("test").dataType(ArrayType.LONG_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BIGINT_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_BIGINT_ARRAY, typeDefine.getDataType());

        column = PhysicalColumn.builder().name("test").dataType(ArrayType.FLOAT_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_FLOAT_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_FLOAT_ARRAY, typeDefine.getDataType());

        column =
                PhysicalColumn.builder().name("test").dataType(ArrayType.DOUBLE_ARRAY_TYPE).build();

        typeDefine = DorisTypeConverter.INSTANCE.reconvert(column);
        Assertions.assertEquals(column.getName(), typeDefine.getName());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DOUBLE_ARRAY, typeDefine.getColumnType());
        Assertions.assertEquals(DorisTypeConverter.DORIS_DOUBLE_ARRAY, typeDefine.getDataType());
    }
}
