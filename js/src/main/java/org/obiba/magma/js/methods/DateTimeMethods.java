package org.obiba.magma.js.methods;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.obiba.magma.Value;
import org.obiba.magma.js.MagmaJsEvaluationRuntimeException;
import org.obiba.magma.js.ScriptableValue;
import org.obiba.magma.type.BooleanType;
import org.obiba.magma.type.DateType;
import org.obiba.magma.type.IntegerType;

/**
 * Methods of the {@code ScriptableValue} javascript class that deal with {@code ScriptableValue} of {@code DateType}.
 */
public class DateTimeMethods {

  /**
   * <pre>
   *   $('Date').year()
   * </pre>
   */
  public static Scriptable year(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.YEAR);
  }

  /**
   * Returns the month of a Date as an integer starting from 0 (January).
   * 
   * <pre>
   *   $('Date').month()
   * </pre>
   */
  public static Scriptable month(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.MONTH);
  }

  /**
   * Returns the day of week from a Date as an integer starting from 1 (Sunday).
   * 
   * <pre>
   *   $('Date').dayOfWeek()
   * </pre>
   */
  public static Scriptable dayOfWeek(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.DAY_OF_WEEK);
  }

  /**
   * Returns a boolean value indicating whether the date denotes a weekday (between Monday and Friday inclusively)
   * 
   * <pre>
   *   $('Date').weekday()
   * </pre>
   */
  public static Scriptable weekday(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Calendar c = asCalendar(thisObj);
    if(c != null) {
      int dow = c.get(Calendar.DAY_OF_WEEK);
      return new ScriptableValue(thisObj, BooleanType.get().valueOf(dow > Calendar.SUNDAY && dow < Calendar.SATURDAY));
    }
    return thisObj;
  }

  /**
   * Returns a boolean value indicating whether the date denotes a weekend (either Sunday or Saturday)
   * 
   * <pre>
   *   $('Date').weekend()
   * </pre>
   */
  public static Scriptable weekend(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Calendar c = asCalendar(thisObj);
    if(c != null) {
      int dow = c.get(Calendar.DAY_OF_WEEK);
      return new ScriptableValue(thisObj, BooleanType.get().valueOf(dow < Calendar.MONDAY && dow > Calendar.FRIDAY));
    }
    return thisObj;
  }

  /**
   * Returns the day of month from a Date as an integer starting from 1
   * 
   * <pre>
   *   $('Date').dayOfMonth()
   * </pre>
   */
  public static Scriptable dayOfMonth(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.DAY_OF_MONTH);
  }

  /**
   * Returns the day of year from a Date as an integer starting from 1
   * 
   * <pre>
   *   $('Date').dayOfYear()
   * </pre>
   */
  public static Scriptable dayOfYear(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.DAY_OF_YEAR);
  }

  /**
   * Returns the week of year from a Date as an integer starting from 1
   * 
   * <pre>
   *   $('Date').weekOfYear()
   * </pre>
   */
  public static Scriptable weekOfYear(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.WEEK_OF_YEAR);
  }

  /**
   * Returns the week of month from a Date as an integer starting from 1
   * 
   * <pre>
   *   $('Date').weekOfMonth()
   * </pre>
   */
  public static Scriptable weekOfMonth(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    return asScriptable(thisObj, thisObj, Calendar.WEEK_OF_MONTH);
  }

  /**
   * Returns true if this Date value is after the specified date value(s)
   * 
   * <pre>
   *   $('Date').after($('OtherDate'))
   *   $('Date').after($('OtherDate'), $('SomeOtherDate'))
   * </pre>
   */
  public static Scriptable after(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
    Calendar thisCalendar = asCalendar(thisObj);
    if(thisCalendar == null) {
      return new ScriptableValue(thisObj, BooleanType.get().nullValue());
    }

    if(args == null || args.length == 0) {
      return new ScriptableValue(thisObj, BooleanType.get().falseValue());
    }

    for(Object arg : args) {
      if(arg instanceof ScriptableValue) {
        ScriptableValue operand = (ScriptableValue) arg;
        Calendar c = asCalendar(operand);
        if(c == null) {
          return new ScriptableValue(thisObj, BooleanType.get().nullValue());
        }
        if(thisCalendar.before(c)) {
          return new ScriptableValue(thisObj, BooleanType.get().falseValue());
        }
      } else {
        throw new MagmaJsEvaluationRuntimeException("Operand to after() method must be a ScriptableValue.");
      }
    }
    return new ScriptableValue(thisObj, BooleanType.get().trueValue());
  }

  /**
   * Converts a {@code ScriptableValue} instance to a {@code Calendar} instance. If {@code Value#isNull()} returns true,
   * this method returns null.
   * 
   * @param obj
   * @return
   */
  private static Calendar asCalendar(Scriptable obj) {
    ScriptableValue sv = (ScriptableValue) obj;
    if(sv.getValueType() != DateType.get()) {
      throw new MagmaJsEvaluationRuntimeException("Invalid ValueType: expected '" + DateType.get().getName() + "' got '" + sv.getValueType().getName() + "'");
    }
    Value value = sv.getValue();
    if(value.isNull() == false) {
      Date date = (Date) value.getValue();
      Calendar c = GregorianCalendar.getInstance();
      c.setTimeInMillis(date.getTime());
      return c;
    }
    return null;
  }

  /**
   * Given a {@code ScriptableValue}, this method extracts a {@code field} from the Calendar.
   * 
   * @param scope
   * @param obj
   * @param field
   * @return
   */
  private static Scriptable asScriptable(Scriptable scope, Scriptable value, int field) {
    Calendar c = asCalendar(value);
    if(c != null) {
      return asScriptable(scope, c.get(field));
    }
    return new ScriptableValue(scope, IntegerType.get().nullValue());
  }

  private static Scriptable asScriptable(Scriptable scope, int value) {
    return new ScriptableValue(scope, IntegerType.get().valueOf(value));
  }

}
