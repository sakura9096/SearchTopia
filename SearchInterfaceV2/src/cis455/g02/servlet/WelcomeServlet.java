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

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import cis455.api.YelpHelper;
import cis455.queryProcess.QueryProcessWrapper;
import cis455.storage.TestDB;

public class WelcomeServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		

		pw.println("<!DOCTYPE html><html lang=\"en\">");
		pw.println("<head><title>Search Engine</title>");
		pw.println("<meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">"
				+ "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>"
				+ "<script src=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js\"></script> "
				+ "<link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css\">"
				+ "<script src=\"http://code.jquery.com/jquery-1.10.2.js\"></script>"
				+ " <script src=\"http://code.jquery.com/ui/1.11.4/jquery-ui.js\"></script>"
				+ "<script type='text/javascript'>//<![CDATA[\n"
				+ "$(document).ready(function() { "
				+ "var availableTags = ['history', 'way', 'art', 'money', 'world', 'information', 'map', 'two', 'family', 'government', 'health', 'system', 'computer', 'meat', 'year', 'thanks', 'music', 'person', 'reading', 'method', 'data', 'food', 'understanding', 'theory', 'law', 'bird', 'literature', 'problem', 'software', 'control', 'knowledge', 'power', 'ability', 'economics', 'love', 'internet', 'television', 'science', 'library', 'nature', 'fact', 'product', 'idea', 'temperature', 'investment', 'area', 'society', 'activity', 'story', 'industry', 'media', 'thing', 'oven', 'community', 'definition', 'safety', 'quality', 'development', 'language', 'management', 'player', 'variety', 'video', 'week', 'security', 'country', 'exam', 'movie', 'organization', 'equipment', 'physics', 'analysis', 'policy', 'series', 'thought', 'basis', 'boyfriend', 'direction', 'strategy', 'technology', 'army', 'camera', 'freedom', 'paper', 'environment', 'child', 'instance', 'month', 'truth', 'marketing', 'university', 'writing', 'article', 'department', 'difference', 'goal', 'news', 'audience', 'fishing', 'growth', 'income', 'marriage', 'user', 'combination', 'failure', 'meaning', 'medicine', 'philosophy', 'teacher', 'communication', 'night', 'chemistry', 'disease', 'disk', 'energy', 'nation', 'road', 'role', 'soup', 'advertising', 'location', 'success', 'addition', 'apartment', 'education', 'math', 'moment', 'painting', 'politics', 'attention', 'decision', 'event', 'property', 'shopping', 'student', 'wood', 'competition', 'distribution', 'entertainment', 'office', 'population', 'president', 'unit', 'category', 'cigarette', 'context', 'introduction', 'opportunity', 'performance', 'driver', 'flight', 'length', 'magazine', 'newspaper', 'relationship', 'teaching', 'cell', 'dealer', 'debate', 'finding', 'lake', 'member', 'message', 'phone', 'scene', 'appearance', 'association', 'concept', 'customer', 'death', 'discussion', 'housing', 'inflation', 'insurance', 'woman', 'advice', 'blood', 'effort', 'expression', 'importance', 'opinion', 'payment', 'reality', 'responsibility', 'situation', 'skill', 'statement', 'wealth', 'application', 'city', 'county', 'depth', 'estate', 'foundation', 'grandmother', 'heart', 'perspective', 'photo', 'recipe', 'studio', 'topic', 'collection', 'depression', 'imagination', 'passion', 'percentage', 'resource', 'setting', 'ad', 'agency', 'college', 'connection', 'criticism', 'debt', 'description', 'memory', 'patience', 'secretary', 'solution', 'administration', 'aspect', 'attitude', 'director', 'personality', 'psychology', 'recommendation', 'response', 'selection', 'storage', 'version', 'alcohol', 'argument', 'complaint', 'contract', 'emphasis', 'highway', 'loss', 'membership', 'possession', 'preparation', 'steak', 'union', 'agreement', 'cancer', 'currency', 'employment', 'engineering', 'entry', 'interaction', 'limit', 'mixture', 'preference', 'region', 'republic', 'seat', 'tradition', 'virus', 'actor', 'classroom', 'delivery', 'device', 'difficulty', 'drama', 'election', 'engine', 'football', 'guidance', 'hotel', 'match', 'owner', 'priority', 'protection', 'suggestion', 'tension', 'variation', 'anxiety', 'atmosphere', 'awareness', 'bread', 'climate', 'comparison', 'confusion', 'construction', 'elevator', 'emotion', 'employee', 'employer', 'guest', 'height', 'leadership', 'mall', 'manager', 'operation', 'recording', 'respect', 'sample', 'transportation', 'boring', 'charity', 'cousin', 'disaster', 'editor', 'efficiency', 'excitement', 'extent', 'feedback', 'guitar', 'homework', 'leader', 'mom', 'outcome', 'permission', 'presentation', 'promotion', 'reflection', 'refrigerator', 'resolution', 'revenue', 'session', 'singer', 'tennis', 'basket', 'bonus', 'cabinet', 'childhood', 'church', 'clothes', 'coffee', 'dinner', 'drawing', 'hair', 'hearing', 'initiative', 'judgment', 'lab', 'measurement', 'mode', 'mud', 'orange', 'poetry', 'police', 'possibility', 'procedure', 'queen', 'ratio', 'relation', 'restaurant', 'satisfaction', 'sector', 'signature', 'significance', 'song', 'tooth', 'town', 'vehicle', 'volume', 'wife', 'accident', 'airport', 'appointment', 'arrival', 'assumption', 'baseball', 'chapter', 'committee', 'conversation', 'database', 'enthusiasm', 'error', 'explanation', 'farmer', 'gate', 'girl', 'hall', 'historian', 'hospital', 'injury', 'instruction', 'maintenance', 'manufacturer', 'meal', 'perception', 'pie', 'poem', 'presence', 'proposal', 'reception', 'replacement', 'revolution', 'river', 'son', 'speech', 'tea', 'village', 'warning', 'winner', 'worker', 'writer', 'assistance', 'breath', 'buyer', 'chest', 'chocolate', 'conclusion', 'contribution', 'cookie', 'courage', 'dad', 'desk', 'drawer', 'establishment', 'examination', 'garbage', 'grocery', 'honey', 'impression', 'improvement', 'independence', 'insect', 'inspection', 'inspector', 'king', 'ladder', 'menu', 'penalty', 'piano', 'potato', 'profession', 'professor', 'quantity', 'reaction', 'requirement', 'salad', 'sister', 'supermarket', 'tongue', 'weakness', 'wedding', 'affair', 'ambition', 'analyst', 'apple', 'assignment', 'assistant', 'bathroom', 'bedroom', 'beer', 'birthday', 'celebration', 'championship', 'cheek', 'client', 'consequence', 'departure', 'diamond', 'dirt', 'ear', 'fortune', 'friendship', 'funeral', 'gene', 'girlfriend', 'hat', 'indication', 'intention', 'lady', 'midnight', 'negotiation', 'obligation', 'passenger', 'pizza', 'platform', 'poet', 'pollution', 'recognition', 'reputation', 'shirt', 'sir', 'speaker', 'stranger', 'surgery', 'sympathy', 'tale', 'throat', 'trainer', 'uncle', 'youth', 'time', 'work', 'film', 'water', 'example', 'while', 'business', 'study', 'game', 'life', 'form', 'air', 'day', 'place', 'number', 'part', 'field', 'fish', 'back', 'process', 'heat', 'hand', 'experience', 'job', 'book', 'end', 'point', 'type', 'home', 'economy', 'value', 'body', 'market', 'guide', 'interest', 'state', 'radio', 'course', 'company', 'price', 'size', 'card', 'list', 'mind', 'trade', 'line', 'care', 'group', 'risk', 'word', 'fat', 'force', 'key', 'light', 'training', 'name', 'school', 'top', 'amount', 'level', 'order', 'practice', 'research', 'sense', 'service', 'piece', 'web', 'boss', 'sport', 'fun', 'house', 'page', 'term', 'test', 'answer', 'sound', 'focus', 'matter', 'kind', 'soil', 'board', 'oil', 'picture', 'access', 'garden', 'range', 'rate', 'reason', 'future', 'site', 'demand', 'exercise', 'image', 'case', 'cause', 'coast', 'action', 'age', 'bad', 'boat', 'record', 'result', 'section', 'building', 'mouse', 'cash', 'class', 'nothing', 'period', 'plan', 'store', 'tax', 'side', 'subject', 'space', 'rule', 'stock', 'weather', 'chance', 'figure', 'man', 'model', 'source', 'beginning', 'earth', 'program', 'chicken', 'design', 'feature', 'head', 'material', 'purpose', 'question', 'rock', 'salt', 'act', 'birth', 'car', 'dog', 'object', 'scale', 'sun', 'note', 'profit', 'rent', 'speed', 'style', 'war', 'bank', 'craft', 'half', 'inside', 'outside', 'standard', 'bus', 'exchange', 'eye', 'fire', 'position', 'pressure', 'stress', 'advantage', 'benefit', 'box', 'frame', 'issue', 'step', 'cycle', 'face', 'item', 'metal', 'paint', 'review', 'room', 'screen', 'structure', 'view', 'account', 'ball', 'discipline', 'medium', 'share', 'balance', 'bit', 'black', 'bottom', 'choice', 'gift', 'impact', 'machine', 'shape', 'tool', 'wind', 'address', 'average', 'career', 'culture', 'morning', 'pot', 'sign', 'table', 'task', 'condition', 'contact', 'credit', 'egg', 'hope', 'ice', 'network', 'north', 'square', 'attempt', 'date', 'effect', 'link', 'post', 'star', 'voice', 'capital', 'challenge', 'friend', 'self', 'shot', 'brush', 'couple', 'exit', 'front', 'function', 'lack', 'living', 'plant', 'plastic', 'spot', 'summer', 'taste', 'theme', 'track', 'wing', 'brain', 'button', 'click', 'desire', 'foot', 'gas', 'influence', 'mood', 'notice', 'rain', 'wall', 'base', 'damage', 'distance', 'feeling', 'pair', 'saving', 'staff', 'sugar', 'target', 'text', 'animal', 'author', 'budget', 'discount', 'file', 'ground', 'lesson', 'minute', 'officer', 'phase', 'reference', 'register', 'sky', 'stage', 'stick', 'title', 'trouble', 'bowl', 'bridge', 'campaign', 'character', 'club', 'edge', 'evidence', 'fan', 'letter', 'lock', 'maximum', 'novel', 'option', 'pack', 'park', 'plenty', 'quarter', 'skin', 'sort', 'weight', 'baby', 'background', 'carry', 'dish', 'factor', 'fruit', 'glass', 'joint', 'master', 'muscle', 'red', 'strength', 'traffic', 'trip', 'vegetable', 'appeal', 'chart', 'gear', 'ideal', 'kitchen', 'land', 'log', 'mother', 'net', 'party', 'principle', 'relative', 'sale', 'season', 'signal', 'spirit', 'street', 'tree', 'wave', 'belt', 'bench', 'commission', 'copy', 'drop', 'minimum', 'path', 'progress', 'project', 'sea', 'south', 'status', 'stuff', 'ticket', 'tour', 'angle', 'blue', 'breakfast', 'confidence', 'daughter', 'degree', 'doctor', 'dot', 'dream', 'duty', 'essay', 'father', 'fee', 'finance', 'hour', 'juice', 'luck', 'milk', 'mouth', 'peace', 'pipe', 'stable', 'storm', 'substance', 'team', 'trick', 'afternoon', 'bat', 'beach', 'blank', 'catch', 'chain', 'consideration', 'cream', 'crew', 'detail', 'gold', 'interview', 'kid', 'mark', 'mission', 'pain', 'pleasure', 'score', 'screw', 'sex', 'shop', 'shower', 'suit', 'tone', 'window', 'agent', 'band', 'bath', 'block', 'bone', 'calendar', 'candidate', 'cap', 'coat', 'contest', 'corner', 'court', 'cup', 'district', 'door', 'east', 'finger', 'garage', 'guarantee', 'hole', 'hook', 'implement', 'layer', 'lecture', 'lie', 'manner', 'meeting', 'nose', 'parking', 'partner', 'profile', 'rice', 'routine', 'schedule', 'swimming', 'telephone', 'tip', 'winter', 'airline', 'bag', 'battle', 'bed', 'bill', 'bother', 'cake', 'code', 'curve', 'designer', 'dimension', 'dress', 'ease', 'emergency', 'evening', 'extension', 'farm', 'fight', 'gap', 'grade', 'holiday', 'horror', 'horse', 'host', 'husband', 'loan', 'mistake', 'mountain', 'nail', 'noise', 'occasion', 'package', 'patient', 'pause', 'phrase', 'proof', 'race', 'relief', 'sand', 'sentence', 'shoulder', 'smoke', 'stomach', 'string', 'tourist', 'towel', 'vacation', 'west', 'wheel', 'wine', 'arm', 'aside', 'associate', 'bet', 'blow', 'border', 'branch', 'breast', 'brother', 'buddy', 'bunch', 'chip', 'coach', 'cross', 'document', 'draft', 'dust', 'expert', 'floor', 'god', 'golf', 'habit', 'iron', 'judge', 'knife', 'landscape', 'league', 'mail', 'mess', 'native', 'opening', 'parent', 'pattern', 'pin', 'pool', 'pound', 'request', 'salary', 'shame', 'shelter', 'shoe', 'silver', 'tackle', 'tank', 'trust', 'assist', 'bake', 'bar', 'bell', 'bike', 'blame', 'boy', 'brick', 'chair', 'closet', 'clue', 'collar', 'comment', 'conference', 'devil', 'diet', 'fear', 'fuel', 'glove', 'jacket', 'lunch', 'monitor', 'mortgage', 'nurse', 'pace', 'panic', 'peak', 'plane', 'reward', 'row', 'sandwich', 'shock', 'spite', 'spray', 'surprise', 'till', 'transition', 'weekend', 'welcome', 'yard', 'alarm', 'bend', 'bicycle', 'bite', 'blind', 'bottle', 'cable', 'candle', 'clerk', 'cloud', 'concert', 'counter', 'flower', 'grandfather', 'harm', 'knee', 'lawyer', 'leather', 'load', 'mirror', 'neck', 'pension', 'plate', 'purple', 'ruin', 'ship', 'skirt', 'slice', 'snow', 'specialist', 'stroke', 'switch', 'trash', 'tune', 'zone', 'anger', 'award', 'bid', 'bitter', 'boot', 'bug', 'camp', 'candy', 'carpet', 'cat', 'champion', 'channel', 'clock', 'comfort', 'cow', 'crack', 'engineer', 'entrance', 'fault', 'grass', 'guy', 'hell', 'highlight', 'incident', 'island', 'joke', 'jury', 'leg', 'lip', 'mate', 'motor', 'nerve', 'passage', 'pen', 'pride', 'priest', 'prize', 'promise', 'resident', 'resort', 'ring', 'roof', 'rope', 'sail', 'scheme', 'script', 'sock', 'station', 'toe', 'tower', 'truck', 'witness', 'a', 'you', 'it', 'can', 'will', 'if', 'one', 'many', 'most', 'other', 'use', 'make', 'good', 'look', 'help', 'go', 'great', 'being', 'few', 'might', 'still', 'public', 'read', 'keep', 'start', 'give', 'human', 'local', 'general', 'she', 'specific', 'long', 'play', 'feel', 'high', 'tonight', 'put', 'common', 'set', 'change', 'simple', 'past', 'big', 'possible', 'particular', 'today', 'major', 'personal', 'current', 'national', 'cut', 'natural', 'physical', 'show', 'try', 'check', 'second', 'call', 'move', 'pay', 'let', 'increase', 'single', 'individual', 'turn', 'ask', 'buy', 'guard', 'hold', 'main', 'offer', 'potential', 'professional', 'international', 'travel', 'cook', 'alternative', 'following', 'special', 'working', 'whole', 'dance', 'excuse', 'cold', 'commercial', 'low', 'purchase', 'deal', 'primary', 'worth', 'fall', 'necessary', 'positive', 'produce', 'search', 'present', 'spend', 'talk', 'creative', 'tell', 'cost', 'drive', 'green', 'support', 'glad', 'remove', 'return', 'run', 'complex', 'due', 'effective', 'middle', 'regular', 'reserve', 'independent', 'leave', 'original', 'reach', 'rest', 'serve', 'watch', 'beautiful', 'charge', 'active', 'break', 'negative', 'safe', 'stay', 'visit', 'visual', 'affect', 'cover', 'report', 'rise', 'walk', 'white', 'beyond', 'junior', 'pick', 'unique', 'anything', 'classic', 'final', 'lift', 'mix', 'private', 'stop', 'teach', 'western', 'concern', 'familiar', 'fly', 'official', 'broad', 'comfortable', 'gain', 'maybe', 'rich', 'save', 'stand', 'young', 'heavy', 'hello', 'lead', 'listen', 'valuable', 'worry', 'handle', 'leading', 'meet', 'release', 'sell', 'finish', 'normal', 'press', 'ride', 'secret', 'spread', 'spring', 'tough', 'wait', 'brown', 'deep', 'display', 'flow', 'hit', 'objective', 'shoot', 'touch', 'cancel', 'chemical', 'cry', 'dump', 'extreme', 'push', 'conflict', 'eat', 'fill', 'formal', 'jump', 'kick', 'opposite', 'pass', 'pitch', 'remote', 'total', 'treat', 'vast', 'abuse', 'beat', 'burn', 'deposit', 'print', 'raise', 'sleep', 'somewhere', 'advance', 'anywhere', 'consist', 'dark', 'double', 'draw', 'equal', 'fix', 'hire', 'internal', 'join', 'kill', 'sensitive', 'tap', 'win', 'attack', 'claim', 'constant', 'drag', 'drink', 'guess', 'minor', 'pull', 'raw', 'soft', 'solid', 'wear', 'weird', 'wonder', 'annual', 'count', 'dead', 'doubt', 'feed', 'forever', 'impress', 'nobody', 'repeat', 'round', 'sing', 'slide', 'strip', 'whereas', 'wish', 'combine', 'command', 'dig', 'divide', 'equivalent', 'hang', 'hunt', 'initial', 'march', 'mention', 'spiritual', 'survey', 'tie', 'adult', 'brief', 'crazy', 'escape', 'gather', 'hate', 'prior', 'repair', 'rough', 'sad', 'scratch', 'sick', 'strike', 'employ', 'external', 'hurt', 'illegal', 'laugh', 'lay', 'mobile', 'nasty', 'ordinary', 'respond', 'royal', 'senior', 'split', 'strain', 'struggle', 'swim', 'train', 'upper', 'wash', 'yellow', 'convert', 'crash', 'dependent', 'fold', 'funny', 'grab', 'hide', 'miss', 'permit', 'quote', 'recover', 'resolve', 'roll', 'sink', 'slip', 'spare', 'suspect', 'sweet', 'swing', 'twist', 'upstairs', 'usual', 'abroad', 'brave', 'calm', 'concentrate', 'estimate', 'grand', 'male', 'mine', 'prompt', 'quiet', 'refuse', 'regret', 'reveal', 'rush', 'shake', 'shift', 'shine', 'steal', 'suck', 'surround', 'anybody', 'bear', 'brilliant', 'dare', 'dear', 'delay', 'drunk', 'female', 'hurry', 'inevitable', 'invite', 'kiss', 'neat', 'pop', 'punch', 'quit', 'reply', 'representative', 'resist', 'rip', 'rub', 'silly', 'smile', 'spell', 'stretch', 'stupid', 'tear', 'temporary', 'tomorrow', 'wake', 'wrap', 'yesterday'];"
				+ "$( \"#tags\" ).autocomplete({"
				+ "source: availableTags});});</script>");
		pw.println("<style> body { background: url(" + request.getContextPath() + "/header.png); background-size: cover; padding-top: 150px;} ");
		pw.println("</style>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<h1 style = \"font-family: fantasy; font-size:400%\" align=\"center\" ><font color=\"white\">SearchTopia</font></h1>"
				+ "<h3 style = \"font-size:150%; font-family: times\" align=\"center\"><font color=\"white\">The Most <strong>Simple</strong> and <strong>Powerful<br> Way</strong> to <strong>Search</strong></font></h3>"
				+ "<br>"
				+ "<form action=\"welcome\" method=\"post\" align=\"center\">"
				+ "<font color=\"Black\"><input class=\"ui-widget\" id=\"tags\" type=\"text\" placeholder=\"Please enter your query\" name=\"query\" style=\"width: 500px; height: 35px\"></font>"
				+ "<button type=\"submit\" class=\"btn btn-secondary-outline\">Search</button>"
				+ "</form>");

		pw.println("</body></html>");
	
		pw.flush();
		pw.close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		String spellmsg = "";
		String finalQuery = "";
		String firstQuery = request.getParameter("query");
		String secondQuery = request.getParameter("decide");
		
		
		HttpResponse<JsonNode> spellcheck;
		if (secondQuery == null) {
			firstQuery = firstQuery.trim();
			finalQuery = firstQuery;
			try {
				spellcheck = Unirest.get("https://montanaflynn-spellcheck.p.mashape.com/check/?text=" + this.spellCheckQuery(firstQuery))
						.header("X-Mashape-Key", "4LiSJm40mdmsh8EgGYw128TMAY8cp1thFKijsnZ4BwP9iZaZhS")
						.header("Accept", "application/json")
						.asJson();
			} catch (UnirestException e) {
				spellcheck = null;
			}
			if (spellcheck != null) {
				JSONObject myObj =  spellcheck.getBody().getObject();

				// extract fields from the object
				spellmsg = myObj.getString("suggestion");
				//System.out.println(msg);
			}
			
			if (!spellmsg.equals("") && !spellmsg.equals(firstQuery)) {
				firstQuery = spellmsg;
			}
		} else {
			finalQuery = secondQuery.trim();
		}
		
		
		/********************************************TO DO**********************************/
		System.out.println("finalQuery:    " + finalQuery);
		List<String> results = processQuery(finalQuery);
		if (results.size() != 100) {
			for (int i = results.size(); i < 100; i++) {
				results.add(""); // just in case they don't return 100 results
			}
		}
		
		/****************************Deal with the database, and return top k**********************************/
	
		
		TestDB db = new TestDB();
		pw.println("<!DOCTYPE html><html lang=\"en\"><head>");
		pw.println(" <title>Search Results</title><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ " <script type='text/javascript' src='https://code.jquery.com/jquery-1.11.3.min.js'>"
				+ "</script><script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/wiki.js\"></script>"
				+ "<script type='text/javascript' src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js\"></script>"
				+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css\">"
				+ "<script type='text/javascript' src=\"https://esimakin.github.io/twbs-pagination/js/jquery.twbsPagination.js\"></script> "
				+ "<link rel=\"stylesheet\" href=\"http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css\">"
				+ "<script src=\"http://code.jquery.com/ui/1.11.4/jquery-ui.js\"></script>"
				+ "<script type='text/javascript'>//<![CDATA[\n "
				+ "$(document).ready(function() {"
				+ "var availableTags =["
				+ "'history', 'way', 'art', 'money', 'world', 'information', 'map', 'two', 'family', 'government', 'health', 'system', 'computer', 'meat', 'year', 'thanks', 'music', 'person', 'reading', 'method', 'data', 'food', 'understanding', 'theory', 'law', 'bird', 'literature', 'problem', 'software', 'control', 'knowledge', 'power', 'ability', 'economics', 'love', 'internet', 'television', 'science', 'library', 'nature', 'fact', 'product', 'idea', 'temperature', 'investment', 'area', 'society', 'activity', 'story', 'industry', 'media', 'thing', 'oven', 'community', 'definition', 'safety', 'quality', 'development', 'language', 'management', 'player', 'variety', 'video', 'week', 'security', 'country', 'exam', 'movie', 'organization', 'equipment', 'physics', 'analysis', 'policy', 'series', 'thought', 'basis', 'boyfriend', 'direction', 'strategy', 'technology', 'army', 'camera', 'freedom', 'paper', 'environment', 'child', 'instance', 'month', 'truth', 'marketing', 'university', 'writing', 'article', 'department', 'difference', 'goal', 'news', 'audience', 'fishing', 'growth', 'income', 'marriage', 'user', 'combination', 'failure', 'meaning', 'medicine', 'philosophy', 'teacher', 'communication', 'night', 'chemistry', 'disease', 'disk', 'energy', 'nation', 'road', 'role', 'soup', 'advertising', 'location', 'success', 'addition', 'apartment', 'education', 'math', 'moment', 'painting', 'politics', 'attention', 'decision', 'event', 'property', 'shopping', 'student', 'wood', 'competition', 'distribution', 'entertainment', 'office', 'population', 'president', 'unit', 'category', 'cigarette', 'context', 'introduction', 'opportunity', 'performance', 'driver', 'flight', 'length', 'magazine', 'newspaper', 'relationship', 'teaching', 'cell', 'dealer', 'debate', 'finding', 'lake', 'member', 'message', 'phone', 'scene', 'appearance', 'association', 'concept', 'customer', 'death', 'discussion', 'housing', 'inflation', 'insurance', 'woman', 'advice', 'blood', 'effort', 'expression', 'importance', 'opinion', 'payment', 'reality', 'responsibility', 'situation', 'skill', 'statement', 'wealth', 'application', 'city', 'county', 'depth', 'estate', 'foundation', 'grandmother', 'heart', 'perspective', 'photo', 'recipe', 'studio', 'topic', 'collection', 'depression', 'imagination', 'passion', 'percentage', 'resource', 'setting', 'ad', 'agency', 'college', 'connection', 'criticism', 'debt', 'description', 'memory', 'patience', 'secretary', 'solution', 'administration', 'aspect', 'attitude', 'director', 'personality', 'psychology', 'recommendation', 'response', 'selection', 'storage', 'version', 'alcohol', 'argument', 'complaint', 'contract', 'emphasis', 'highway', 'loss', 'membership', 'possession', 'preparation', 'steak', 'union', 'agreement', 'cancer', 'currency', 'employment', 'engineering', 'entry', 'interaction', 'limit', 'mixture', 'preference', 'region', 'republic', 'seat', 'tradition', 'virus', 'actor', 'classroom', 'delivery', 'device', 'difficulty', 'drama', 'election', 'engine', 'football', 'guidance', 'hotel', 'match', 'owner', 'priority', 'protection', 'suggestion', 'tension', 'variation', 'anxiety', 'atmosphere', 'awareness', 'bread', 'climate', 'comparison', 'confusion', 'construction', 'elevator', 'emotion', 'employee', 'employer', 'guest', 'height', 'leadership', 'mall', 'manager', 'operation', 'recording', 'respect', 'sample', 'transportation', 'boring', 'charity', 'cousin', 'disaster', 'editor', 'efficiency', 'excitement', 'extent', 'feedback', 'guitar', 'homework', 'leader', 'mom', 'outcome', 'permission', 'presentation', 'promotion', 'reflection', 'refrigerator', 'resolution', 'revenue', 'session', 'singer', 'tennis', 'basket', 'bonus', 'cabinet', 'childhood', 'church', 'clothes', 'coffee', 'dinner', 'drawing', 'hair', 'hearing', 'initiative', 'judgment', 'lab', 'measurement', 'mode', 'mud', 'orange', 'poetry', 'police', 'possibility', 'procedure', 'queen', 'ratio', 'relation', 'restaurant', 'satisfaction', 'sector', 'signature', 'significance', 'song', 'tooth', 'town', 'vehicle', 'volume', 'wife', 'accident', 'airport', 'appointment', 'arrival', 'assumption', 'baseball', 'chapter', 'committee', 'conversation', 'database', 'enthusiasm', 'error', 'explanation', 'farmer', 'gate', 'girl', 'hall', 'historian', 'hospital', 'injury', 'instruction', 'maintenance', 'manufacturer', 'meal', 'perception', 'pie', 'poem', 'presence', 'proposal', 'reception', 'replacement', 'revolution', 'river', 'son', 'speech', 'tea', 'village', 'warning', 'winner', 'worker', 'writer', 'assistance', 'breath', 'buyer', 'chest', 'chocolate', 'conclusion', 'contribution', 'cookie', 'courage', 'dad', 'desk', 'drawer', 'establishment', 'examination', 'garbage', 'grocery', 'honey', 'impression', 'improvement', 'independence', 'insect', 'inspection', 'inspector', 'king', 'ladder', 'menu', 'penalty', 'piano', 'potato', 'profession', 'professor', 'quantity', 'reaction', 'requirement', 'salad', 'sister', 'supermarket', 'tongue', 'weakness', 'wedding', 'affair', 'ambition', 'analyst', 'apple', 'assignment', 'assistant', 'bathroom', 'bedroom', 'beer', 'birthday', 'celebration', 'championship', 'cheek', 'client', 'consequence', 'departure', 'diamond', 'dirt', 'ear', 'fortune', 'friendship', 'funeral', 'gene', 'girlfriend', 'hat', 'indication', 'intention', 'lady', 'midnight', 'negotiation', 'obligation', 'passenger', 'pizza', 'platform', 'poet', 'pollution', 'recognition', 'reputation', 'shirt', 'sir', 'speaker', 'stranger', 'surgery', 'sympathy', 'tale', 'throat', 'trainer', 'uncle', 'youth', 'time', 'work', 'film', 'water', 'example', 'while', 'business', 'study', 'game', 'life', 'form', 'air', 'day', 'place', 'number', 'part', 'field', 'fish', 'back', 'process', 'heat', 'hand', 'experience', 'job', 'book', 'end', 'point', 'type', 'home', 'economy', 'value', 'body', 'market', 'guide', 'interest', 'state', 'radio', 'course', 'company', 'price', 'size', 'card', 'list', 'mind', 'trade', 'line', 'care', 'group', 'risk', 'word', 'fat', 'force', 'key', 'light', 'training', 'name', 'school', 'top', 'amount', 'level', 'order', 'practice', 'research', 'sense', 'service', 'piece', 'web', 'boss', 'sport', 'fun', 'house', 'page', 'term', 'test', 'answer', 'sound', 'focus', 'matter', 'kind', 'soil', 'board', 'oil', 'picture', 'access', 'garden', 'range', 'rate', 'reason', 'future', 'site', 'demand', 'exercise', 'image', 'case', 'cause', 'coast', 'action', 'age', 'bad', 'boat', 'record', 'result', 'section', 'building', 'mouse', 'cash', 'class', 'nothing', 'period', 'plan', 'store', 'tax', 'side', 'subject', 'space', 'rule', 'stock', 'weather', 'chance', 'figure', 'man', 'model', 'source', 'beginning', 'earth', 'program', 'chicken', 'design', 'feature', 'head', 'material', 'purpose', 'question', 'rock', 'salt', 'act', 'birth', 'car', 'dog', 'object', 'scale', 'sun', 'note', 'profit', 'rent', 'speed', 'style', 'war', 'bank', 'craft', 'half', 'inside', 'outside', 'standard', 'bus', 'exchange', 'eye', 'fire', 'position', 'pressure', 'stress', 'advantage', 'benefit', 'box', 'frame', 'issue', 'step', 'cycle', 'face', 'item', 'metal', 'paint', 'review', 'room', 'screen', 'structure', 'view', 'account', 'ball', 'discipline', 'medium', 'share', 'balance', 'bit', 'black', 'bottom', 'choice', 'gift', 'impact', 'machine', 'shape', 'tool', 'wind', 'address', 'average', 'career', 'culture', 'morning', 'pot', 'sign', 'table', 'task', 'condition', 'contact', 'credit', 'egg', 'hope', 'ice', 'network', 'north', 'square', 'attempt', 'date', 'effect', 'link', 'post', 'star', 'voice', 'capital', 'challenge', 'friend', 'self', 'shot', 'brush', 'couple', 'exit', 'front', 'function', 'lack', 'living', 'plant', 'plastic', 'spot', 'summer', 'taste', 'theme', 'track', 'wing', 'brain', 'button', 'click', 'desire', 'foot', 'gas', 'influence', 'mood', 'notice', 'rain', 'wall', 'base', 'damage', 'distance', 'feeling', 'pair', 'saving', 'staff', 'sugar', 'target', 'text', 'animal', 'author', 'budget', 'discount', 'file', 'ground', 'lesson', 'minute', 'officer', 'phase', 'reference', 'register', 'sky', 'stage', 'stick', 'title', 'trouble', 'bowl', 'bridge', 'campaign', 'character', 'club', 'edge', 'evidence', 'fan', 'letter', 'lock', 'maximum', 'novel', 'option', 'pack', 'park', 'plenty', 'quarter', 'skin', 'sort', 'weight', 'baby', 'background', 'carry', 'dish', 'factor', 'fruit', 'glass', 'joint', 'master', 'muscle', 'red', 'strength', 'traffic', 'trip', 'vegetable', 'appeal', 'chart', 'gear', 'ideal', 'kitchen', 'land', 'log', 'mother', 'net', 'party', 'principle', 'relative', 'sale', 'season', 'signal', 'spirit', 'street', 'tree', 'wave', 'belt', 'bench', 'commission', 'copy', 'drop', 'minimum', 'path', 'progress', 'project', 'sea', 'south', 'status', 'stuff', 'ticket', 'tour', 'angle', 'blue', 'breakfast', 'confidence', 'daughter', 'degree', 'doctor', 'dot', 'dream', 'duty', 'essay', 'father', 'fee', 'finance', 'hour', 'juice', 'luck', 'milk', 'mouth', 'peace', 'pipe', 'stable', 'storm', 'substance', 'team', 'trick', 'afternoon', 'bat', 'beach', 'blank', 'catch', 'chain', 'consideration', 'cream', 'crew', 'detail', 'gold', 'interview', 'kid', 'mark', 'mission', 'pain', 'pleasure', 'score', 'screw', 'sex', 'shop', 'shower', 'suit', 'tone', 'window', 'agent', 'band', 'bath', 'block', 'bone', 'calendar', 'candidate', 'cap', 'coat', 'contest', 'corner', 'court', 'cup', 'district', 'door', 'east', 'finger', 'garage', 'guarantee', 'hole', 'hook', 'implement', 'layer', 'lecture', 'lie', 'manner', 'meeting', 'nose', 'parking', 'partner', 'profile', 'rice', 'routine', 'schedule', 'swimming', 'telephone', 'tip', 'winter', 'airline', 'bag', 'battle', 'bed', 'bill', 'bother', 'cake', 'code', 'curve', 'designer', 'dimension', 'dress', 'ease', 'emergency', 'evening', 'extension', 'farm', 'fight', 'gap', 'grade', 'holiday', 'horror', 'horse', 'host', 'husband', 'loan', 'mistake', 'mountain', 'nail', 'noise', 'occasion', 'package', 'patient', 'pause', 'phrase', 'proof', 'race', 'relief', 'sand', 'sentence', 'shoulder', 'smoke', 'stomach', 'string', 'tourist', 'towel', 'vacation', 'west', 'wheel', 'wine', 'arm', 'aside', 'associate', 'bet', 'blow', 'border', 'branch', 'breast', 'brother', 'buddy', 'bunch', 'chip', 'coach', 'cross', 'document', 'draft', 'dust', 'expert', 'floor', 'god', 'golf', 'habit', 'iron', 'judge', 'knife', 'landscape', 'league', 'mail', 'mess', 'native', 'opening', 'parent', 'pattern', 'pin', 'pool', 'pound', 'request', 'salary', 'shame', 'shelter', 'shoe', 'silver', 'tackle', 'tank', 'trust', 'assist', 'bake', 'bar', 'bell', 'bike', 'blame', 'boy', 'brick', 'chair', 'closet', 'clue', 'collar', 'comment', 'conference', 'devil', 'diet', 'fear', 'fuel', 'glove', 'jacket', 'lunch', 'monitor', 'mortgage', 'nurse', 'pace', 'panic', 'peak', 'plane', 'reward', 'row', 'sandwich', 'shock', 'spite', 'spray', 'surprise', 'till', 'transition', 'weekend', 'welcome', 'yard', 'alarm', 'bend', 'bicycle', 'bite', 'blind', 'bottle', 'cable', 'candle', 'clerk', 'cloud', 'concert', 'counter', 'flower', 'grandfather', 'harm', 'knee', 'lawyer', 'leather', 'load', 'mirror', 'neck', 'pension', 'plate', 'purple', 'ruin', 'ship', 'skirt', 'slice', 'snow', 'specialist', 'stroke', 'switch', 'trash', 'tune', 'zone', 'anger', 'award', 'bid', 'bitter', 'boot', 'bug', 'camp', 'candy', 'carpet', 'cat', 'champion', 'channel', 'clock', 'comfort', 'cow', 'crack', 'engineer', 'entrance', 'fault', 'grass', 'guy', 'hell', 'highlight', 'incident', 'island', 'joke', 'jury', 'leg', 'lip', 'mate', 'motor', 'nerve', 'passage', 'pen', 'pride', 'priest', 'prize', 'promise', 'resident', 'resort', 'ring', 'roof', 'rope', 'sail', 'scheme', 'script', 'sock', 'station', 'toe', 'tower', 'truck', 'witness', 'a', 'you', 'it', 'can', 'will', 'if', 'one', 'many', 'most', 'other', 'use', 'make', 'good', 'look', 'help', 'go', 'great', 'being', 'few', 'might', 'still', 'public', 'read', 'keep', 'start', 'give', 'human', 'local', 'general', 'she', 'specific', 'long', 'play', 'feel', 'high', 'tonight', 'put', 'common', 'set', 'change', 'simple', 'past', 'big', 'possible', 'particular', 'today', 'major', 'personal', 'current', 'national', 'cut', 'natural', 'physical', 'show', 'try', 'check', 'second', 'call', 'move', 'pay', 'let', 'increase', 'single', 'individual', 'turn', 'ask', 'buy', 'guard', 'hold', 'main', 'offer', 'potential', 'professional', 'international', 'travel', 'cook', 'alternative', 'following', 'special', 'working', 'whole', 'dance', 'excuse', 'cold', 'commercial', 'low', 'purchase', 'deal', 'primary', 'worth', 'fall', 'necessary', 'positive', 'produce', 'search', 'present', 'spend', 'talk', 'creative', 'tell', 'cost', 'drive', 'green', 'support', 'glad', 'remove', 'return', 'run', 'complex', 'due', 'effective', 'middle', 'regular', 'reserve', 'independent', 'leave', 'original', 'reach', 'rest', 'serve', 'watch', 'beautiful', 'charge', 'active', 'break', 'negative', 'safe', 'stay', 'visit', 'visual', 'affect', 'cover', 'report', 'rise', 'walk', 'white', 'beyond', 'junior', 'pick', 'unique', 'anything', 'classic', 'final', 'lift', 'mix', 'private', 'stop', 'teach', 'western', 'concern', 'familiar', 'fly', 'official', 'broad', 'comfortable', 'gain', 'maybe', 'rich', 'save', 'stand', 'young', 'heavy', 'hello', 'lead', 'listen', 'valuable', 'worry', 'handle', 'leading', 'meet', 'release', 'sell', 'finish', 'normal', 'press', 'ride', 'secret', 'spread', 'spring', 'tough', 'wait', 'brown', 'deep', 'display', 'flow', 'hit', 'objective', 'shoot', 'touch', 'cancel', 'chemical', 'cry', 'dump', 'extreme', 'push', 'conflict', 'eat', 'fill', 'formal', 'jump', 'kick', 'opposite', 'pass', 'pitch', 'remote', 'total', 'treat', 'vast', 'abuse', 'beat', 'burn', 'deposit', 'print', 'raise', 'sleep', 'somewhere', 'advance', 'anywhere', 'consist', 'dark', 'double', 'draw', 'equal', 'fix', 'hire', 'internal', 'join', 'kill', 'sensitive', 'tap', 'win', 'attack', 'claim', 'constant', 'drag', 'drink', 'guess', 'minor', 'pull', 'raw', 'soft', 'solid', 'wear', 'weird', 'wonder', 'annual', 'count', 'dead', 'doubt', 'feed', 'forever', 'impress', 'nobody', 'repeat', 'round', 'sing', 'slide', 'strip', 'whereas', 'wish', 'combine', 'command', 'dig', 'divide', 'equivalent', 'hang', 'hunt', 'initial', 'march', 'mention', 'spiritual', 'survey', 'tie', 'adult', 'brief', 'crazy', 'escape', 'gather', 'hate', 'prior', 'repair', 'rough', 'sad', 'scratch', 'sick', 'strike', 'employ', 'external', 'hurt', 'illegal', 'laugh', 'lay', 'mobile', 'nasty', 'ordinary', 'respond', 'royal', 'senior', 'split', 'strain', 'struggle', 'swim', 'train', 'upper', 'wash', 'yellow', 'convert', 'crash', 'dependent', 'fold', 'funny', 'grab', 'hide', 'miss', 'permit', 'quote', 'recover', 'resolve', 'roll', 'sink', 'slip', 'spare', 'suspect', 'sweet', 'swing', 'twist', 'upstairs', 'usual', 'abroad', 'brave', 'calm', 'concentrate', 'estimate', 'grand', 'male', 'mine', 'prompt', 'quiet', 'refuse', 'regret', 'reveal', 'rush', 'shake', 'shift', 'shine', 'steal', 'suck', 'surround', 'anybody', 'bear', 'brilliant', 'dare', 'dear', 'delay', 'drunk', 'female', 'hurry', 'inevitable', 'invite', 'kiss', 'neat', 'pop', 'punch', 'quit', 'reply', 'representative', 'resist', 'rip', 'rub', 'silly', 'smile', 'spell', 'stretch', 'stupid', 'tear', 'temporary', 'tomorrow', 'wake', 'wrap', 'yesterday'];"
				+"$( \"#tags\" ).autocomplete({"
				+ "source: availableTags"
				+ "});});</script>"
				+ "<script type='text/javascript'>//<![CDATA[\n"
				+ "$(document).ready(function() {"
				+ "$('#demo1').WikipediaWidget('" + this.wikiQuery(finalQuery) + "');"
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
//					String url = results.get(j);
//					Document doc = Jsoup.connect(url).get();
//					String title = doc.title();
					String url = results.get(j);
					String title = db.getTitle(url);
					
					//String title = url;
					if  (title == null) {
						
						title = url;
					}
					
					title = title.replaceAll("\"", "'");
					url = url.replaceAll("\"", "'");
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
				+ "<input class=\"ui-widget\" id=\"tags\" type=\"text\" placeholder=\"Please enter your query\" name=\"query\" style=\"width: 500px; height: 40px\">"
				+ "<button type=\"submit\" class=\"btn btn-secondary-outline btn-md\">Search</button></form></div></nav>");
		if (!spellmsg.equals(finalQuery) && !spellmsg.equals("") ) {
			pw.println("<h4 style = \"font-family: sans-serif\">Displaying results for " + finalQuery + ".  "
					+ "<form name='myform' action=\"welcome\" method=\"post\">Do you still want to search: <input type=\"hidden\" name=\"decide\" value='" + firstQuery + "'><span onclick=\"document.myform.submit();\"><u><strong>" + firstQuery +  "</strong></u></span></form></h4>");
			
		}
		pw.println(""
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
				+ "<li class=\"active\"><a data-toggle=\"tab\" href=\"#sectionA\">What's Wikipedia Saying</a></li>"
				+ "<li><a data-toggle=\"tab\" href=\"#sectionB\">Yelp Businesses</a></li>"
			
				+ "</ul>"
				+ "<div class=\"tab-content\">"
				+ "<div id=\"sectionA\" class=\"tab-pane fade in active\">"
						+ "<div id=\"demo1\" class=\"wikipediaContainer\"><div class=\"bg\"></div></div>"
						+ "</div>"
						+ "<div id=\"sectionB\" class=\"tab-pane fade\">"
						+ "<h4>Business Information</h4>"
						+ "<p>" + this.yelpHTML(finalQuery) + "</p>"
						
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
//		List<String> results = new ArrayList<String>();
//	
//		for (int i = 0; i < 10; i++) {
//			results.add("https://en.wikipedia.org/wiki/2003");
//		}
//		for (int i = 10; i < 20; i++) {
//			results.add("https://en.wikipedia.org/wiki/1909");
//		}
//		for (int i = 20; i < 30; i++) {
//			results.add("https://en.wikipedia.org/wiki/2003");
//		}
//		for (int i = 30; i < 40; i++) {
//			results.add("https://en.wikipedia.org/wiki/1909");
//		}
//		for (int i = 40; i < 50; i++) {
//			results.add("https://en.wikipedia.org/wiki/2003");
//		}
//		for (int i = 50; i < 60; i++) {
//			results.add("https://en.wikipedia.org/wiki/1909");
//		}
//		for (int i = 60; i < 70; i++) {
//			results.add("https://en.wikipedia.org/wiki/2003");
//		}
//		for (int i = 70; i < 80; i++) {
//			results.add("https://en.wikipedia.org/wiki/1909");
//		}
//		for (int i = 80; i < 90; i++) {
//			results.add("https://en.wikipedia.org/wiki/2003");
//		}
//		for (int i = 90; i < 100; i++) {
//			results.add("https://en.wikipedia.org/wiki/1909");
//		}
//		
//		/*This part needs to be removed!*/
		QueryProcessWrapper process = new QueryProcessWrapper();
		//System.out.println(process.getQueryResult(query));
		return process.getQueryResult(query);
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
	
	public String spellCheckQuery(String query) {
		String[] strings = query.split("\\s+");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String item : strings) {
			if (first) first = false;
			else
		      sb.append("+");
		      sb.append(item);
		   }
		return sb.toString();
	}

}
