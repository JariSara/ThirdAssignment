/*
 * generated by Xtext 2.25.0
 */
package dk.sdu.mmmi.mdsd.validation

//import dk.sdu.mmmi.mdsd.math.MathExp
import dk.sdu.mmmi.mdsd.math.MathPackage
import dk.sdu.mmmi.mdsd.math.VarBinding
import org.eclipse.xtext.validation.Check
import dk.sdu.mmmi.mdsd.math.Program

class MathValidator extends AbstractMathValidator {

	public static final String VAR_UNIQUE = 'var_unique'
	
	@Check
	def void uniqueGlobalVariableDefinition(VarBinding binding){
		if((binding.eContainer as Program).variables.filter[name == binding.name].size > 1)
			error("Duplicate global variable", MathPackage.eINSTANCE.binding_Name, VAR_UNIQUE)
	}
	
}
