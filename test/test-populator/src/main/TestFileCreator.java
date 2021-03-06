package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * {@link TestFileCreator} can be used to create the needed test files
 * for the {@link TestProblem}.
 * @author Vicky H. Tanzil
 */
public class TestFileCreator {
	
	/**
	 * The extension for the test files.
	 */
	private static final String extension = ".m";
	
	/**
	 * The test problem.
	 */
	private TestProblem testProblem;

	/**
	 * The test function f in the test problem.
	 */
	private TestFunction f;

	/**
	 * The name of the file defining the function definition.
	 */
	private String defFileName;
	
	/**
	 * The name of the file defining the function gradient.
	 */
	private String gradFileName;
	
	/**
	 * The name of the file defining the function hessian matrix.
	 */
	private String hessFileName;

	/**
	 * The path to the directory where all created files should be placed.
	 */
	private String directoryPath;
	
	private TestProblem2LaTeX tp2tex;
	
	/**
	 * Create MFileCreator for the given test problem.
	 * @param testProblem The test problem.
	 * @param directoryPath The directory where the created files should be placed. 
	 */
	public TestFileCreator(TestProblem testProblem, String directoryPath) {
		this.testProblem = testProblem;
		tp2tex = new TestProblem2LaTeX(testProblem);
		f = testProblem.getTestFunction();
		defFileName = f.getName();
		gradFileName = "grad_" + defFileName;
		hessFileName = "hess_" + defFileName;
		if (directoryPath.endsWith("/")) {
			directoryPath = directoryPath.substring(0, directoryPath.length()-1);
		}
		File dir = new File(directoryPath);
		if (!dir.exists()) {
			if (dir.mkdirs()) {
				if (Main.printInfo) {
					System.out.println("Create directory: " + directoryPath);
				}
			}
		}
		this.directoryPath = directoryPath;
	}
	
	private void createFunctionDefinitionFile() {
		String fileName = defFileName;
		// create the definition file for function f
		String content = "%" + tp2tex.getStringForTestFile().replaceAll("\n", "\n%")
							+ "\n";
		content += "function y = " + fileName + "(" + f.getVar() + ")\n";
		if (f.getConstantNames().size() != 0) {
			for (String s : f.getConstantNames()) {
				content += "\t" + s + " = " + f.getConstantValue(s) + ";\n";
			}
		}
		content += "\t" + f.getDefinition() + "\n";
		content += "end";
		createFile(fileName+extension, content);
	}
	
	private void createFunctionGradientFile() {
		String fileName = gradFileName;
		// create the file for the gradient of function f
		String content = "function g = " + fileName + "(" + f.getVar() + ")\n";
		if (f.getConstantNames().size() != 0) {
			for (String s : f.getConstantNames()) {
				content += "\t" + s + " = " + f.getConstantValue(s) + ";\n";
			}
		}
		content += "\t" + f.getGradient() + "\n";
		content += "end";
		createFile(fileName+extension, content);
	}

	private void createFunctionHessianFile() {
		String fileName = hessFileName;
		// create the file for the hessian matrix of function f
		String content = "function H = " + fileName + "(" + f.getVar() + ")\n";
		if (f.getConstantNames().size() != 0) {
			for (String s : f.getConstantNames()) {
				content += "\t" + s + " = " + f.getConstantValue(s) + ";\n";
			}
		}
		content += "\t" + f.getHessianMatrix() + "\n";
		content += "end";
		createFile(fileName+extension, content);
	}
	
	/**
	 * Create test file for this problems
	 * @param fileNamePattern The name pattern of the test file.
	 * The word "problem" will be replaced with the problem name.
	 * @param templateFilePath The template for the test file
	 */
	private void createTestFile(String fileNamePattern, String templateFilePath) {
		String fileName = fileNamePattern.replace("problem", testProblem.getName());
		String content = getTestFileContentUsingTemplate(templateFilePath);
		createFile(fileName+extension, content);
	}
	
	/**
	 * Get the content of the test file.
	 * @param templateFilePath The template file defining the test.
	 * The variables placed within this file should be
	 * started with '{var_' and ended with '}'.
	 * @return The content of the test file.
	 */
	private String getTestFileContentUsingTemplate(String templateFilePath) {
		// define the variables to be replaced in the template file
		HashMap<String, String> vars = new HashMap<String, String>();
		vars.put("{var_problem_name}", testProblem.getName());
		vars.put("{var_function_name}", defFileName);
		vars.put("{var_grad_function_name}", gradFileName);
		vars.put("{var_hess_function_name}", hessFileName);
		vars.put("{var_A}", testProblem.get_A());
		vars.put("{var_b}", testProblem.get_b());
		vars.put("{var_G}", testProblem.get_G());
		vars.put("{var_r}", testProblem.get_r());
		vars.put("{var_u}", testProblem.get_u());
		vars.put("{var_v}", testProblem.get_v());
		vars.put("{var_x0}", testProblem.get_x0());
		vars.put("{var_n}", testProblem.getDimension()+"");
		vars.put("{var_tol}", testProblem.getTolerance());
		vars.put("{var_itmax}", testProblem.getMaxIteration());
		
		// get the content of the template file
		String content = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(templateFilePath));
			String line = "";
			while ( (line=br.readLine()) != null ) {
				content += line + "\n";
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// replace the variables in the templates content
		for (String s : vars.keySet()) {
			while (content.contains(s)) {
				content = content.replace(s, vars.get(s));
			}
		}
		return content;
	}
	
	/**
	 * Write a file.
	 * @param fileName The file name.
	 * @param fileContens The content of the file.
	 */
	private void createFile(String fileName, String fileContens) {
		if (!directoryPath.equals("")) {
			fileName = directoryPath + "/" + fileName;
		}
		try {
			FileWriter fileWriter = new FileWriter(fileName);
			fileWriter.write(fileContens);
			fileWriter.flush();
			fileWriter.close();
			if (Main.printInfo) {
				System.out.println("File created: " + fileName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create all files needed for the given test problem.
	 * @param testProblem The test problem.
	 * @param directoryPath The directory to place the files for the test problem.
	 * @param testTemplates A hash map with test template name pattern as key
	 * and path to template file as value.
	 */
	public static void create(TestProblem testProblem,
			String directoryPath, HashMap<String,String> testTemplates) {
		TestFileCreator testFileCreator = new TestFileCreator(testProblem, directoryPath);
		testFileCreator.createFunctionDefinitionFile();
		testFileCreator.createFunctionGradientFile();
		testFileCreator.createFunctionHessianFile();
		for (String s : testTemplates.keySet()) {
			testFileCreator.createTestFile(s, testTemplates.get(s));
		}
	}
	
	/**
	 * Create a test file which called each test file of each problem.
	 * @param problemsList A collection containing all test problems.
	 * @param filePrefix The prefix for this test file.
	 * @param directoryPath The directory to place this file.
	 * @param testNamePattern The name pattern of the tests available.
	 */
	public static void createMainTestFile(Collection<TestProblem> problemsList,
			String filePrefix, String directoryPath, Set<String> testNamePattern) {
		if (problemsList.isEmpty()) {
			return;
		}
		for (String namePattern : testNamePattern) {
			String fileName = namePattern.replace("test_problem", filePrefix);
			String content = "function " + fileName + "()\n";
			content += "\twarning('off','all')\n";
			TestProblem dummy = null;
			for (TestProblem p : problemsList) {
				String problemTestFunction = namePattern.replace("problem", p.getName());
				content += "\tdisp('test_" + p.getName() + "');\n";
				if (!namePattern.contains("with_fmincon")) {
					content += "\t" + problemTestFunction + "(1);\n";
				} else {
					content += "\t" + problemTestFunction + "();\n";
				}
				content += "\tdisp(sprintf('\\n'));\n";
				dummy = p;
			}
			content += "end";
			// create a dummy MFileCreator object, to be able to use the createFile() method
			TestFileCreator testFileCreator = new TestFileCreator(dummy, directoryPath);
			testFileCreator.createFile(fileName+extension, content);
		}
	}

}