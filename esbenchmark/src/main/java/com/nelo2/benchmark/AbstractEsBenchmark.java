package com.nelo2.benchmark;
/**
 * 
 * @author Administrator
 *
 */
public abstract class AbstractEsBenchmark {
	
	public abstract String name();

	public abstract String benchmark();
	
	public  String praseHtml(String name,int size,String totalTime,double evarageTime){
		StringBuffer bf = new StringBuffer();
		bf.append("<table class=\"table\">");
		bf.append("<thead><tr><th>name</th><th>size</th><th>total time</th><th>average time</th></tr></thead>");
		bf.append("<tbody>").append("<tr><td>").append(name).append("</td>").append("<td>").append(size).append("</td>");
		bf.append("<td>").append(totalTime).append("</td>").append("<td>").append(evarageTime).append("</td>");
		bf.append("</tr></tbody></table>");
		return bf.toString();
	}
	
}
