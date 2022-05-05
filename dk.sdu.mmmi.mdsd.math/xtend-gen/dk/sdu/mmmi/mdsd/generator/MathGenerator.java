package dk.sdu.mmmi.mdsd.generator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import dk.sdu.mmmi.mdsd.math.Binding;
import dk.sdu.mmmi.mdsd.math.Div;
import dk.sdu.mmmi.mdsd.math.ExternalDef;
import dk.sdu.mmmi.mdsd.math.LetBinding;
import dk.sdu.mmmi.mdsd.math.MathNumber;
import dk.sdu.mmmi.mdsd.math.Minus;
import dk.sdu.mmmi.mdsd.math.Mult;
import dk.sdu.mmmi.mdsd.math.Plus;
import dk.sdu.mmmi.mdsd.math.Program;
import dk.sdu.mmmi.mdsd.math.VarBinding;
import dk.sdu.mmmi.mdsd.math.VariableUse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;

@SuppressWarnings("all")
public class MathGenerator extends AbstractGenerator {
  private static Map<String, Integer> variables;
  
  private static String computeString = "";
  
  @Override
  public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
    final Program program = Iterators.<Program>filter(resource.getAllContents(), Program.class).next();
    final EList<VarBinding> vars = program.getVariables();
    final Map<String, Integer> result = MathGenerator.compute(vars);
    this.displayPanel(result);
    this.generateFile(program, fsa);
    MathGenerator.variables = null;
    MathGenerator.computeString = "";
  }
  
  public void generateFile(final Program program, final IFileSystemAccess2 fsa) {
    String _name = program.getName();
    String _plus = (_name + ".java");
    fsa.generateFile(_plus, this.generateProgramFile(program));
  }
  
  public CharSequence generateProgramFile(final Program program) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("package math_expression;");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("public class ");
    String _name = program.getName();
    _builder.append(_name);
    _builder.append("{");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    CharSequence _generateVariables = this.generateVariables(program);
    _builder.append(_generateVariables, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.newLine();
    {
      int _size = program.getExternals().size();
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        _builder.append("\t");
        _builder.append("private External external;");
        _builder.newLine();
        _builder.append("\t");
        _builder.newLine();
      }
    }
    _builder.append("\t");
    CharSequence _generateConstructor = this.generateConstructor(program);
    _builder.append(_generateConstructor, "\t");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    _builder.append("\t");
    CharSequence _generateCompute = this.generateCompute(program);
    _builder.append(_generateCompute, "\t");
    _builder.newLineIfNotEmpty();
    _builder.newLine();
    {
      int _size_1 = program.getExternals().size();
      boolean _greaterThan_1 = (_size_1 > 0);
      if (_greaterThan_1) {
        _builder.append("\t");
        _builder.append("interface External {");
        _builder.newLine();
        _builder.append("\t");
        _builder.append("\t");
        CharSequence _generateExternals = this.generateExternals(program);
        _builder.append(_generateExternals, "\t\t");
        _builder.newLineIfNotEmpty();
        _builder.append("\t");
        _builder.append("}");
        _builder.newLine();
      }
    }
    _builder.append("}");
    return _builder;
  }
  
  public CharSequence generateVariables(final Program program) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<VarBinding> _variables = program.getVariables();
      for(final VarBinding variable : _variables) {
        _builder.append("public int ");
        String _name = variable.getName();
        _builder.append(_name);
        _builder.append(";");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public CharSequence generateConstructor(final Program program) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public ");
    String _name = program.getName();
    _builder.append(_name);
    _builder.append("(");
    {
      int _size = program.getExternals().size();
      boolean _greaterThan = (_size > 0);
      if (_greaterThan) {
        _builder.append("External external");
      }
    }
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    {
      int _size_1 = program.getExternals().size();
      boolean _greaterThan_1 = (_size_1 > 0);
      if (_greaterThan_1) {
        _builder.append("this.external = external;");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence generateExternals(final Program program) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<ExternalDef> _externals = program.getExternals();
      for(final ExternalDef external : _externals) {
        {
          String _name = external.getName();
          boolean _equals = Objects.equal(_name, "sqrt");
          if (_equals) {
            _builder.append("public int sqrt(int n);");
          }
        }
        _builder.newLineIfNotEmpty();
        {
          String _name_1 = external.getName();
          boolean _equals_1 = Objects.equal(_name_1, "pow");
          if (_equals_1) {
            _builder.append("public int pow(int n, int m);");
          }
        }
        _builder.newLineIfNotEmpty();
        {
          String _name_2 = external.getName();
          boolean _equals_2 = Objects.equal(_name_2, "pi");
          if (_equals_2) {
            _builder.append("public int pi();");
          }
        }
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  public CharSequence generateCompute(final Program program) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public void compute(){");
    _builder.newLine();
    _builder.append("\t");
    _builder.append(MathGenerator.computeString, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public void displayPanel(final Map<String, Integer> result) {
    String resultString = "";
    Set<Map.Entry<String, Integer>> _entrySet = result.entrySet();
    for (final Map.Entry<String, Integer> entry : _entrySet) {
      String _resultString = resultString;
      String _key = entry.getKey();
      String _plus = ("var " + _key);
      String _plus_1 = (_plus + " = ");
      Integer _value = entry.getValue();
      String _plus_2 = (_plus_1 + _value);
      String _plus_3 = (_plus_2 + "\n");
      resultString = (_resultString + _plus_3);
    }
    JOptionPane.showMessageDialog(null, resultString, "Math Language", JOptionPane.INFORMATION_MESSAGE);
  }
  
  public static Map<String, Integer> compute(final List<VarBinding> math) {
    Map<String, Integer> _xblockexpression = null;
    {
      HashMap<String, Integer> _hashMap = new HashMap<String, Integer>();
      MathGenerator.variables = _hashMap;
      for (final VarBinding varBinding : math) {
        MathGenerator.computeExpression(varBinding);
      }
      _xblockexpression = MathGenerator.variables;
    }
    return _xblockexpression;
  }
  
  protected static int _computeExpression(final VarBinding binding) {
    String _computeString = MathGenerator.computeString;
    String _name = binding.getName();
    String _plus = (_name + " = ");
    MathGenerator.computeString = (_computeString + _plus);
    MathGenerator.variables.put(binding.getName(), Integer.valueOf(MathGenerator.computeExpression(binding.getExpression())));
    String _computeString_1 = MathGenerator.computeString;
    MathGenerator.computeString = (_computeString_1 + ";\n");
    return (MathGenerator.variables.get(binding.getName())).intValue();
  }
  
  protected static int _computeExpression(final MathNumber exp) {
    int _xblockexpression = (int) 0;
    {
      String _computeString = MathGenerator.computeString;
      String _string = Integer.valueOf(exp.getValue()).toString();
      MathGenerator.computeString = (_computeString + _string);
      _xblockexpression = exp.getValue();
    }
    return _xblockexpression;
  }
  
  protected static int _computeExpression(final Plus exp) {
    int _xblockexpression = (int) 0;
    {
      int valueLeft = MathGenerator.computeExpression(exp.getLeft());
      String _computeString = MathGenerator.computeString;
      MathGenerator.computeString = (_computeString + " + ");
      int valueRight = MathGenerator.computeExpression(exp.getRight());
      _xblockexpression = (valueLeft + valueRight);
    }
    return _xblockexpression;
  }
  
  protected static int _computeExpression(final Minus exp) {
    int _xblockexpression = (int) 0;
    {
      int valueLeft = MathGenerator.computeExpression(exp.getLeft());
      String _computeString = MathGenerator.computeString;
      MathGenerator.computeString = (_computeString + " - ");
      int valueRight = MathGenerator.computeExpression(exp.getRight());
      _xblockexpression = (valueLeft - valueRight);
    }
    return _xblockexpression;
  }
  
  protected static int _computeExpression(final Mult exp) {
    int _xblockexpression = (int) 0;
    {
      int valueLeft = MathGenerator.computeExpression(exp.getLeft());
      String _computeString = MathGenerator.computeString;
      MathGenerator.computeString = (_computeString + " * ");
      int valueRight = MathGenerator.computeExpression(exp.getRight());
      _xblockexpression = (valueLeft * valueRight);
    }
    return _xblockexpression;
  }
  
  protected static int _computeExpression(final Div exp) {
    int _xblockexpression = (int) 0;
    {
      int valueLeft = MathGenerator.computeExpression(exp.getLeft());
      String _computeString = MathGenerator.computeString;
      MathGenerator.computeString = (_computeString + " / ");
      int valueRight = MathGenerator.computeExpression(exp.getRight());
      _xblockexpression = (valueLeft / valueRight);
    }
    return _xblockexpression;
  }
  
  protected static int _computeExpression(final LetBinding exp) {
    return MathGenerator.computeExpression(exp.getBody());
  }
  
  protected static int _computeExpression(final VariableUse exp) {
    return MathGenerator.computeBinding(exp.getRef());
  }
  
  protected static int _computeBinding(final VarBinding binding) {
    Integer _xblockexpression = null;
    {
      boolean _containsKey = MathGenerator.variables.containsKey(binding.getName());
      boolean _not = (!_containsKey);
      if (_not) {
        MathGenerator.computeExpression(binding);
      }
      String _computeString = MathGenerator.computeString;
      String _name = binding.getName();
      MathGenerator.computeString = (_computeString + _name);
      _xblockexpression = MathGenerator.variables.get(binding.getName());
    }
    return (_xblockexpression).intValue();
  }
  
  protected static int _computeBinding(final LetBinding binding) {
    return MathGenerator.computeExpression(binding.getBinding());
  }
  
  public static int computeExpression(final EObject exp) {
    if (exp instanceof Div) {
      return _computeExpression((Div)exp);
    } else if (exp instanceof LetBinding) {
      return _computeExpression((LetBinding)exp);
    } else if (exp instanceof MathNumber) {
      return _computeExpression((MathNumber)exp);
    } else if (exp instanceof Minus) {
      return _computeExpression((Minus)exp);
    } else if (exp instanceof Mult) {
      return _computeExpression((Mult)exp);
    } else if (exp instanceof Plus) {
      return _computeExpression((Plus)exp);
    } else if (exp instanceof VarBinding) {
      return _computeExpression((VarBinding)exp);
    } else if (exp instanceof VariableUse) {
      return _computeExpression((VariableUse)exp);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(exp).toString());
    }
  }
  
  public static int computeBinding(final Binding binding) {
    if (binding instanceof LetBinding) {
      return _computeBinding((LetBinding)binding);
    } else if (binding instanceof VarBinding) {
      return _computeBinding((VarBinding)binding);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(binding).toString());
    }
  }
}
