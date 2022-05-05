package dk.sdu.mmmi.mdsd.generator

import dk.sdu.mmmi.mdsd.math.Div
import dk.sdu.mmmi.mdsd.math.LetBinding
import dk.sdu.mmmi.mdsd.math.MathNumber
import dk.sdu.mmmi.mdsd.math.Minus
import dk.sdu.mmmi.mdsd.math.Mult
import dk.sdu.mmmi.mdsd.math.Plus
import dk.sdu.mmmi.mdsd.math.VarBinding
import dk.sdu.mmmi.mdsd.math.VariableUse
import java.util.HashMap
import java.util.Map
import javax.swing.JOptionPane
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import dk.sdu.mmmi.mdsd.math.Program
import java.util.List

class MathGenerator extends AbstractGenerator {

	static Map<String, Integer> variables;	
	static String computeString = "";

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		val program = resource.allContents.filter(Program).next
		val vars = program.variables
		val result = vars.compute
		result.displayPanel
		program.generateFile(fsa)
		variables = null;
		computeString = "";
	}

	def void generateFile(Program program, IFileSystemAccess2 fsa) {
		fsa.generateFile(program.name + ".java", generateProgramFile(program))
	}

	def generateProgramFile(Program program) {
		'''
		
		package math_expression;
			
		public class «program.name»{
			
			«program.generateVariables»
					
			«IF program.externals.size() > 0»
			private External external;
			
			«ENDIF»			 
			«program.generateConstructor»
		
			«program.generateCompute»
		
			«IF program.externals.size() > 0»
				interface External {
					«program.generateExternals»
				}
			«ENDIF»
		}'''
	}

	def generateVariables(Program program) '''
		«FOR variable : program.variables»
			public int «variable.name»;
		«ENDFOR»
	'''
	def generateConstructor(Program program) '''
		public «program.name»(«IF program.externals.size() > 0»External external«ENDIF») {
			«IF program.externals.size() > 0»this.external = external;«ENDIF»
		}
	'''
	
	def generateExternals(Program program) '''
		«FOR external : program.externals»
			«IF external.name=='sqrt'»public int sqrt(int n);«ENDIF»
			«IF external.name=='pow'»public int pow(int n, int m);«ENDIF»
			«IF external.name=='pi'»public int pi();«ENDIF»
		«ENDFOR»
	'''
	def generateCompute(Program program) '''
		public void compute(){
			«computeString»
		}
	'''
	def void displayPanel(Map<String, Integer> result) {
		var resultString = ""
		for (entry : result.entrySet()) {
			resultString += "var " + entry.getKey() + " = " + entry.getValue() + "\n"
		}
		JOptionPane.showMessageDialog(null, resultString, "Math Language", JOptionPane.INFORMATION_MESSAGE)
	}
	def static compute(List<VarBinding> math) {
		variables = new HashMap()
		for (varBinding : math)
			varBinding.computeExpression()
		variables
	}
	def static dispatch int computeExpression(VarBinding binding) {
		computeString += binding.name + " = " //+ binding.expression + ";\n"
		variables.put(binding.name, binding.expression.computeExpression())	
		computeString += ";\n"	
		return variables.get(binding.name)
	}
	def static dispatch int computeExpression(MathNumber exp) {
		computeString += exp.value.toString()
		exp.value		
	}
	def static dispatch int computeExpression(Plus exp) {		
		var valueLeft = exp.left.computeExpression;
		computeString += " + "
		var valueRight = exp.right.computeExpression;
		valueLeft + valueRight	
	}
	def static dispatch int computeExpression(Minus exp) {
		var valueLeft = exp.left.computeExpression;
		computeString += " - "
		var valueRight = exp.right.computeExpression;
		valueLeft - valueRight
	}
	def static dispatch int computeExpression(Mult exp) {
		var valueLeft = exp.left.computeExpression;
		computeString += " * "
		var valueRight = exp.right.computeExpression;
		valueLeft * valueRight
	}
	def static dispatch int computeExpression(Div exp) {
		var valueLeft = exp.left.computeExpression;
		computeString += " / "
		var valueRight = exp.right.computeExpression;
		valueLeft / valueRight
	}
	def static dispatch int computeExpression(LetBinding exp) {
		exp.body.computeExpression
	}
	def static dispatch int computeExpression(VariableUse exp) {
		exp.ref.computeBinding
	}
	def static dispatch int computeBinding(VarBinding binding) {
		if (!variables.containsKey(binding.name))
			binding.computeExpression()
		computeString += binding.name
		variables.get(binding.name)
	}
	def static dispatch int computeBinding(LetBinding binding) {
		binding.binding.computeExpression
	}
}