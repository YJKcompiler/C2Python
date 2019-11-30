package listener.main;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javax.swing.*;
import java.util.Arrays;

public class Translator {
	enum OPTIONS {
		PRETTYPRINT, BYTECODEGEN, UCODEGEN, ERROR
	}
	private static OPTIONS getOption(String[] args){
		if (args.length < 1)
			return OPTIONS.BYTECODEGEN;
		
		if (args[0].startsWith("-p") 
				|| args[0].startsWith("-P"))
			return OPTIONS.PRETTYPRINT;
		
		if (args[0].startsWith("-b") 
				|| args[0].startsWith("-B"))
			return OPTIONS.BYTECODEGEN;
		
		if (args[0].startsWith("-u") 
				|| args[0].startsWith("-U"))
			return OPTIONS.UCODEGEN;
		
		return OPTIONS.ERROR;
	}
	
	public static void main(String[] args) throws Exception
	{
		CharStream codeCharStream = CharStreams.fromFileName("/Users/goseonggwan/Downloads/Repo/C2Python/Compiler/test2.c");
		MiniCLexer lexer = new MiniCLexer(codeCharStream);
		CommonTokenStream tokens = new CommonTokenStream( lexer );
		MiniCParser parser = new MiniCParser( tokens );
		ParseTree tree = parser.program();

		JFrame frame = new JFrame("Antlr AST");
		JPanel panel = new JPanel();
		TreeViewer viewr = new TreeViewer(Arrays.asList(
				parser.getRuleNames()),tree);
		frame.add(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1500,600);
		frame.setVisible(true);
		viewr.setScale(0.8);//scale a little
		panel.add(viewr);
		ParseTreeWalker walker = new ParseTreeWalker();

		switch (getOption(args)) {
			/*case PRETTYPRINT :
				walker.walk(new MiniCPrintListener(), tree );
				break;*/
			case BYTECODEGEN:
				walker.walk(new BytecodeGenListener(), tree );

				break;
			/*case UCODEGEN:
				walker.walk(new UCodeGenListener(), tree );
				break;*/
			default:
				break;
		}
	}
}