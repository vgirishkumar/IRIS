package com.temenos.interaction.odataext.odataparser.odata4j;
/*
 * A mutable copy of oData4j's PrintExpressionVisior. This is identical to org.odata4j.expression. PrintExpressionVisior
 * apart from the append() methods are changed from private to protected. So they can be overridden in sub class.
 */

/*
 * #%L
 * interaction-authorization
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import org.odata4j.expression.AddExpression;
import org.odata4j.expression.AggregateAllFunction;
import org.odata4j.expression.AggregateAnyFunction;
import org.odata4j.expression.AndExpression;
import org.odata4j.expression.BinaryLiteral;
import org.odata4j.expression.BoolParenExpression;
import org.odata4j.expression.BooleanLiteral;
import org.odata4j.expression.ByteLiteral;
import org.odata4j.expression.CastExpression;
import org.odata4j.expression.CeilingMethodCallExpression;
import org.odata4j.expression.CommonExpression;
import org.odata4j.expression.ConcatMethodCallExpression;
import org.odata4j.expression.DateTimeLiteral;
import org.odata4j.expression.DateTimeOffsetLiteral;
import org.odata4j.expression.DayMethodCallExpression;
import org.odata4j.expression.DecimalLiteral;
import org.odata4j.expression.DivExpression;
import org.odata4j.expression.DoubleLiteral;
import org.odata4j.expression.EndsWithMethodCallExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.expression.EqExpression;
import org.odata4j.expression.ExpressionParser;
import org.odata4j.expression.ExpressionVisitor;
import org.odata4j.expression.FloorMethodCallExpression;
import org.odata4j.expression.GeExpression;
import org.odata4j.expression.GtExpression;
import org.odata4j.expression.GuidLiteral;
import org.odata4j.expression.HourMethodCallExpression;
import org.odata4j.expression.IndexOfMethodCallExpression;
import org.odata4j.expression.Int64Literal;
import org.odata4j.expression.IntegralLiteral;
import org.odata4j.expression.IsofExpression;
import org.odata4j.expression.LeExpression;
import org.odata4j.expression.LengthMethodCallExpression;
import org.odata4j.expression.LtExpression;
import org.odata4j.expression.MinuteMethodCallExpression;
import org.odata4j.expression.ModExpression;
import org.odata4j.expression.MonthMethodCallExpression;
import org.odata4j.expression.MulExpression;
import org.odata4j.expression.NeExpression;
import org.odata4j.expression.NegateExpression;
import org.odata4j.expression.NotExpression;
import org.odata4j.expression.NullLiteral;
import org.odata4j.expression.OrExpression;
import org.odata4j.expression.OrderByExpression;
import org.odata4j.expression.OrderByExpression.Direction;
import org.odata4j.expression.ParenExpression;
import org.odata4j.expression.ReplaceMethodCallExpression;
import org.odata4j.expression.RoundMethodCallExpression;
import org.odata4j.expression.SByteLiteral;
import org.odata4j.expression.SecondMethodCallExpression;
import org.odata4j.expression.SingleLiteral;
import org.odata4j.expression.StartsWithMethodCallExpression;
import org.odata4j.expression.StringLiteral;
import org.odata4j.expression.SubExpression;
import org.odata4j.expression.SubstringMethodCallExpression;
import org.odata4j.expression.SubstringOfMethodCallExpression;
import org.odata4j.expression.TimeLiteral;
import org.odata4j.expression.ToLowerMethodCallExpression;
import org.odata4j.expression.ToUpperMethodCallExpression;
import org.odata4j.expression.TrimMethodCallExpression;
import org.odata4j.expression.YearMethodCallExpression;
import org.odata4j.internal.InternalUtil;
import org.odata4j.repack.org.apache.commons.codec.binary.Hex;

public class PrintExpressionVisitor implements ExpressionVisitor {

  private final StringBuilder sb = new StringBuilder();

  public static String asString(CommonExpression expr) {
    PrintExpressionVisitor v = new PrintExpressionVisitor();
    expr.visit(v);
    return v.toString();
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  protected void append(String value) {
    sb.append(value);
  }

  protected void append(String format, Object... args) {
    sb.append(String.format(format, args));
  }

  @Override
  public void visit(String type) {
    append(type);
  }

  @Override
  public void afterDescend() {
    append(")");

  }

  @Override
  public void beforeDescend() {
    append("(");

  }

  @Override
  public void betweenDescend() {
    append(",");

  }

  @Override
  public void visit(AddExpression expr) {
    append("add");
  }

  @Override
  public void visit(AndExpression expr) {
    append("and");
  }

  @Override
  public void visit(BooleanLiteral expr) {
    append("boolean(%s)", expr.getValue());
  }

  @Override
  public void visit(CastExpression expr) {
    append("cast");
  }

  @Override
  public void visit(ConcatMethodCallExpression expr) {
    append("concat");
  }

  @Override
  public void visit(DateTimeLiteral expr) {
    append("datetime(%s)", InternalUtil.formatDateTimeForXml(expr.getValue()));
  }

  @Override
  public void visit(DateTimeOffsetLiteral expr) {
    append("datetime(%s)", InternalUtil.formatDateTimeOffsetForXml(expr.getValue()));
  }

  @Override
  public void visit(DecimalLiteral expr) {
    append("decimal(%s)", expr.getValue());
  }

  @Override
  public void visit(DivExpression expr) {
    append("div");
  }

  @Override
  public void visit(EndsWithMethodCallExpression expr) {
    append("endswith");
  }

  @Override
  public void visit(EntitySimpleProperty expr) {
    append("simpleProperty(%s)", expr.getPropertyName());
  }

  @Override
  public void visit(EqExpression expr) {
    append("eq");
  }

  @Override
  public void visit(GeExpression expr) {
    append("ge");
  }

  @Override
  public void visit(GtExpression expr) {
    append("gt");
  }

  @Override
  public void visit(GuidLiteral expr) {
    append("guid(%s)", expr.getValue());
  }

  @Override
  public void visit(IndexOfMethodCallExpression expr) {
    append("indexof");
  }

  @Override
  public void visit(IntegralLiteral expr) {
    append("integral(%s)", expr.getValue());
  }

  @Override
  public void visit(IsofExpression expr) {
    append("isof");
  }

  @Override
  public void visit(LeExpression expr) {
    append("le");
  }

  @Override
  public void visit(LengthMethodCallExpression expr) {
    append("length");
  }

  @Override
  public void visit(LtExpression expr) {
    append("lt");
  }

  @Override
  public void visit(ModExpression expr) {
    append("mod");
  }

  @Override
  public void visit(MulExpression expr) {
    append("mul");
  }

  @Override
  public void visit(NeExpression expr) {
    append("ne");
  }

  @Override
  public void visit(NegateExpression expr) {
    append("negate");
  }

  @Override
  public void visit(NotExpression expr) {
    append("not");
  }

  @Override
  public void visit(NullLiteral expr) {
    append("null");
  }

  @Override
  public void visit(OrExpression expr) {
    append("or");
  }

  @Override
  public void visit(ParenExpression expr) {
    append("paren");
  }

  @Override
  public void visit(BoolParenExpression expr) {
    append("boolParen");
  }

  @Override
  public void visit(ReplaceMethodCallExpression expr) {
    append("replace");
  }

  @Override
  public void visit(StartsWithMethodCallExpression expr) {
    append("startswith");
  }

  @Override
  public void visit(StringLiteral expr) {
    append("string(%s)", expr.getValue());
  }

  @Override
  public void visit(SubExpression expr) {
    append("sub");
  }

  @Override
  public void visit(SubstringMethodCallExpression expr) {
    append("substring");
  }

  @Override
  public void visit(SubstringOfMethodCallExpression expr) {
    append("substringof");
  }

  @Override
  public void visit(TimeLiteral expr) {
    append("time(%s)", expr.getValue().toString(ExpressionParser.TIME_FORMATTER));
  }

  @Override
  public void visit(ToLowerMethodCallExpression expr) {
    append("tolower");
  }

  @Override
  public void visit(ToUpperMethodCallExpression expr) {
    append("toupper");
  }

  @Override
  public void visit(TrimMethodCallExpression expr) {
    append("trim");
  }

  @Override
  public void visit(YearMethodCallExpression expr) {
    append("year");
  }

  @Override
  public void visit(MonthMethodCallExpression expr) {
    append("month");
  }

  @Override
  public void visit(DayMethodCallExpression expr) {
    append("day");
  }

  @Override
  public void visit(HourMethodCallExpression expr) {
    append("hour");
  }

  @Override
  public void visit(MinuteMethodCallExpression expr) {
    append("minute");
  }

  @Override
  public void visit(SecondMethodCallExpression expr) {
    append("second");
  }

  @Override
  public void visit(RoundMethodCallExpression expr) {
    append("round");
  }

  @Override
  public void visit(FloorMethodCallExpression expr) {
    append("floor");
  }

  @Override
  public void visit(CeilingMethodCallExpression expr) {
    append("ceiling");
  }

  @Override
  public void visit(OrderByExpression expr) {
    append("orderBy");
  }

  @Override
  public void visit(Direction direction) {
    append(direction == Direction.ASCENDING ? "asc" : "desc");
  }

  @Override
  public void visit(Int64Literal expr) {
    append("int64(%s)", expr.getValue());
  }

  @Override
  public void visit(SingleLiteral expr) {
    append("single(%s)", expr.getValue());
  }

  @Override
  public void visit(DoubleLiteral expr) {
    append("double(%s)", expr.getValue());
  }

  @Override
  public void visit(BinaryLiteral expr) {
    append("binary(%s)", Hex.encodeHexString(expr.getValue()));
  }

  @Override
  public void visit(ByteLiteral expr) {
    append("byte(%s)", expr.getValue());
  }

  @Override
  public void visit(SByteLiteral expr) {
    append("sbyte(%s)", expr.getValue());
  }

  @Override
  public void visit(AggregateAnyFunction expr) {
    if (expr.getVariable() != null) {
      append("any:(%s =>)", expr.getVariable());
    } else {
      append("any()");
    }
  }

  @Override
  public void visit(AggregateAllFunction expr) {
    append("all:%s =>", expr.getVariable());
  }

}
