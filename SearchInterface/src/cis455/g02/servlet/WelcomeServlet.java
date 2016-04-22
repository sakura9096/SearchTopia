package cis455.g02.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cis455.api.YelpHelper;

public class WelcomeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();

		pw.println("<!DOCTYPE html><html lang=\"en\">");
		pw.println("<head><title>Search Engine</title>");
		pw.println("<meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>"
				+ "<script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script>");
		pw.println("<style> body { background: url(" + request.getContextPath() + "/header.png); background-size: cover; padding-top: 150px;} ");
		pw.println("</style>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<h1 style = \"font-family: fantasy; font-size:400%\" align=\"center\" ><font color=\"white\">SearchTopia</font></h1>"
				+ "<h3 style = \"font-size:150%; font-family: times\" align=\"center\"><font color=\"white\">The Most <strong>Simple</strong> and <strong>Powerful<br> Way</strong> to <strong>Search</strong></font></h3>"
				+ "<br>"
				+ "<form action=\"welcome\" method=\"post\" align=\"center\">"
				+ "<font color=\"Black\"><input type=\"text\" placeholder=\"Please enter your query\" name=\"query\" style=\"width: 500px; height: 35px\"></font>"
				+ "<button type=\"submit\" class=\"btn btn-secondary-outline\">Search</button>"
				+ "</form>");

		pw.println("</body></html>");
	
		pw.flush();
		pw.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		String query = request.getParameter("query").trim();
		/********************************************TO DO**********************************/
		List<String> results = processQuery(query);
		if (results.size() != 100) {
			for (int i = results.size(); i < 100; i++) {
				results.add("https://www.google.com"); // just in case they don't return 100 results
			}
		}
		/****************************Deal with the database, and return top k**********************************/
		pw.println("<!DOCTYPE html><html lang=\"en\"><head>");
		pw.println(" <title>Search Results</title><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ " <script type='text/javascript' src='https://code.jquery.com/jquery-1.11.3.min.js'>"
				+ "</script><script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/wiki.js\"></script>"
				+ "<script type='text/javascript' src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script>"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\"><script type='text/javascript' src=\"https://esimakin.github.io/twbs-pagination/js/jquery.twbsPagination.js\"></script>"
				+ "<script type='text/javascript'>//<![CDATA[\n"
				+ "$(document).ready(function() {"
				+ "$('#demo1').WikipediaWidget('" + this.wikiQuery(query) + "');"
				+ " });</script>"
				+ "<script type='text/javascript'>//<![CDATA[ \n"
				+ "$(window).load(function(){$('#pagination-demo').twbsPagination({"
				+ "totalPages: \"10\","
				+ "visiblePages: \"7\","
				+ "onPageClick: function (event, page) {");
			for (int i = 0; i < 10; i++) {
				int p = i + 1;
				String page = p + "";
				pw.print("if (page ==\"" + page + "\") { $('#page-content').html(\"");
				for (int j = i * 10; j < i * 10 + 10; j++) {
					String url = results.get(j);
					Document doc = Jsoup.connect(url).get();
					String title = doc.title();
					pw.print("<a href='" + url +"' class='list-group-item'><h4 class='list-group-item-heading'>" + title + "</h4><p class='list-group-item-text'>" + url + "</p></a>");
				}
				pw.println("\");}");
			}
			pw.println("}"
							+ "});"
							+ "});//]]>"
				+ "</script>"
				+ " <style>.navbar {margin-bottom: 0;border-radius: 0;} #myNavbar {padding-top: 5px;position: relative;}"
				+ ".row.content {height: 450px}.sidenav {padding-top: 20px;background-color: #f1f1f1;height: 200%; width: 400px;}"
				+ "footer {background-color: #555;color: white;padding: 15px;}"
				+ "@media screen and (max-width: 767px) {"
				+ ".sidenav {height: auto;padding: 15px;}.row.content {height:auto;} }"
				+ ".ajaxLoading {margin-top:50px;text-align:center;} .wikipediaContainer { position:relative;"
				+ "min-height:150px;"
				+ "width:350px;"
				+ "padding:10px;"
				+ "border-radius:5px;"
				+ "background:#ddd;"
				+ "}"
				+ ".wikipediaContainer .bg {"
				+ "position:absolute;"
				+ "bottom:20px;"
				+ "right:20px;"
				+ "width:135px;"
				+ "height:155px;"
				+ "background: no-repeat url('http://upload.wikimedia.org/wikipedia/commons/3/30/Wikipedia_2.0-new_prototype.png');"
				+ "opacity:0.3;"
				+ "}"
				+ ".wikipediaContainer .wikipediaTitle {text-align:center;height:30px;font:20px Verdana bold;border-radius:5px 5px 0 0;background:#fff;margin:-5px 0 10px 0;line-height:30px;}"
				+ ".wikipediaContainer .wikipediaLogo {width:150px;float:left;margin-right:20px;}"
				+ ".wikipediaContainer .wikipediaDescription {float:left;width:330px;}"
				+ ".wikipediaContainer ul.wikipediaThumbGallery {float:left;width:350px;list-style:none;padding:0;}"
				+ ".wikipediaContainer ul.wikipediaThumbGallery li {display:inline-block;float:left;margin:0 10px 10px 0;}"
				+ ".wikipediaContainer table.wikipediaInfoTable {float:left;width:330px;}"
				+ ".wikipediaContainer .clear {clear:both;}"
				+ "</style>");
		pw.println("</head><body>");
		pw.println("<nav class=\"navbar navbar-inverse\"><a class=\"navbar-brand\" style = \"font-family: fantasy; font-size: 150%\"><font color=\"white\">SearchTopia</font></a>"
				+ "<div class=\"navbar-nav\" id=\"myNavbar\">"
				+ "<form action=\"welcome\" method=\"post\" align=\"center\">"
				+ "<input type=\"text\" placeholder=\"Please enter your query\" name=\"query\" style=\"width: 500px; height: 40px\">"
				+ "<button type=\"submit\" class=\"btn btn-secondary-outline btn-md\">Search</button></form></div></nav>"
				+ "<div class=\"container-fluid text-center\"><div class=\"row content\">"
				+ "<div class=\"col-sm-8 text-left\">"
				+ "<div class=\"list-group\">"
				+ "<div id =\"page-content\"></div></div>"
				+ "<ul id=\"pagination-demo\" class=\"pagination pagination-lg\"></ul>"
				+ "</div>"
				+ "<div class=\"col-sm-2 sidenav\">"
				+ "<div class=\"well\">"
				+ "<div class=\"bs-example\">"
				+ "<ul class=\"nav nav-tabs\">"
				+ "<li class=\"active\"><a data-toggle=\"tab\" href=\"#sectionA\">Wikipedia</a></li>"
				+ "<li><a data-toggle=\"tab\" href=\"#sectionB\">Yelp Businesses</a></li>"
			
				+ "</ul>"
				+ "<div class=\"tab-content\">"
				+ "<div id=\"sectionA\" class=\"tab-pane fade in active\">"
						+ "<div id=\"demo1\" class=\"wikipediaContainer\"><div class=\"bg\"></div></div>"
						+ "</div>"
						+ "<div id=\"sectionB\" class=\"tab-pane fade\">"
						+ "<h4>Business Information</h4>"
						+ "<p>" + this.yelpHTML(query) + "</p>"
						
						+ "</div>"
						
						+ "</div>"
						+ "</div>"
				+ "</div>"
				+ "</div>"
				+ "</div><"
				+ "/div>"
				+ "<footer class=\"container-fluid text-center\">"
				+ "<p>@Copyright CIS455G02</p>"
				+ "</footer>");
		pw.println("</body></html>");
		
		
		
		pw.flush();
		pw.close();
	}
	
	public List<String> processQuery(String query) {
		/*This part needs to be removed!*/
		List<String> results = new ArrayList<String>();
	
		for (int i = 0; i < 10; i++) {
			results.add("http://www.apple.com/");
		}
		for (int i = 10; i < 20; i++) {
			results.add("http://www.dell.com/en-us/");
		}
		for (int i = 20; i < 30; i++) {
			results.add("http://www.apple.com/");
		}
		for (int i = 30; i < 40; i++) {
			results.add("http://www.dell.com/en-us/");
		}
		for (int i = 40; i < 50; i++) {
			results.add("http://www.apple.com/");
		}
		for (int i = 50; i < 60; i++) {
			results.add("http://www.dell.com/en-us/");
		}
		for (int i = 60; i < 70; i++) {
			results.add("http://www.apple.com/");
		}
		for (int i = 70; i < 80; i++) {
			results.add("http://www.dell.com/en-us/");
		}
		for (int i = 80; i < 90; i++) {
			results.add("http://www.apple.com/");
		}
		for (int i = 90; i < 100; i++) {
			results.add("http://www.dell.com/en-us/");
		}
		
		/*This part needs to be removed!*/
		return results;
	}
	
	public String wikiQuery(String str) {
		String[] strings = str.split("\\s+");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String item : strings) {
			if (first) first = false;
			else
		      sb.append("_");
		      sb.append(item);
		   }
		return sb.toString();
	}
	
	public String yelpHTML(String str) {
		YelpHelper helper = new YelpHelper(str);
		return helper.excute();
	}

}
