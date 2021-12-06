import java.io.*;
import java.util.*;
import java.awt.Desktop;


//rendre le truc plus un word explicatif sur github en envoyant un mail avec le lien à renau sur discord 



class LireMK {
	public static void main(String[] argv) throws IOException {
		try{
			File fileIn = new File("oui.txt");    
			FileReader in = new FileReader(fileIn);  
			BufferedReader br = new BufferedReader(in);  
			StringBuffer sb = new StringBuffer();
			
			File fileOut = new File("result.txt");
			FileWriter out = new FileWriter(fileOut);
			BufferedWriter output = new BufferedWriter(out);
			
			int indentation = 1;
			String tabulation = "   ";
			int dernierH = 0;
			String line;
			ArrayList<String> historique = new ArrayList<String>();
			String strOut = "{\n";
			output.write("{");
			int precedent = 0;
			while((line = br.readLine()) != null){								
				String balise = identifierType(line);
				String line2 = line;
				
				if(tabulation.length() != 3*indentation){
					tabulation ="";
					for(int i = 0; i<indentation; i++){
						tabulation+="   ";
					}
				}
				if(precedent == 1){
					if(historique.get(historique.size()-1).length() >3){
						if(!"".equals(balise)){
							if("ul".equals(historique.get(historique.size()-1).substring(1,3))){
								balise = "";
								output.write(",\n"+tabulation+"{\"li\": \""+line.substring(2)+"\"}");
							}
						}
						if(historique.get(historique.size()-1).length() >4){
							if("code".equals(historique.get(historique.size()-1).substring(1,5))){
								balise = "";
								if(!"```".equals(line2)){
									if(line2.indexOf("\"") != -1){
										line2 = replace("\"","\\\"", line2);
									}
									if("1".equals(historique.get(historique.size()-1).substring(5,6))){
										output.write("\\n"+line2);
										historique.set(historique.size()-1, "\"code2\"");
									}
									else{
										output.write(line2);
										historique.set(historique.size()-1, "\"code1\"");
									}
								}
							}
						}
					}
				}
				
				if(!"".equals(balise)){
					if("h".equals(balise.substring(1,2))){
						if(Integer.parseInt(balise.substring(2,3))>dernierH){
							if(dernierH!=0){
								output.write(",");
							}
							line2 = "\n"+tabulation+balise+": {\n"+tabulation+"   \"label\": \""+
							line.substring(Integer.parseInt(balise.substring(2,3)))+"\",\n"+
							tabulation+"   \"content\": {";
							output.write(line2);
							precedent = 1;
							indentation+=2;
							dernierH = Integer.parseInt(balise.substring(2,3));
						}
						else if(Integer.parseInt(balise.substring(2,3))==dernierH){
							output.write("\n"+tabulation.substring(3)+"}\n"+tabulation.substring(6)+"},");
							line2 = "\n"+tabulation.substring(6)+balise+": {\n"+tabulation.substring(3)+
							"\"label\": \""+
							line.substring(Integer.parseInt(balise.substring(2,3)))+"\",\n"+
							tabulation.substring(3)+"\"content\": {";
							output.write(line2);
							precedent = 1;
						}
						else if(Integer.parseInt(balise.substring(2,3))<dernierH){
							int val = dernierH - Integer.parseInt(balise.substring(2,3));
							for (int i= 0; i<=val; i++){
								output.write("\n"+tabulation.substring(3+6*i)+"}\n"+tabulation.substring(6+6*i)+"}");
							}
							
							line2 = ",\n"+tabulation.substring(6+6*val)+
							balise+
							": {\n"+
							tabulation.substring(3+6*val)+
							"\"label\": \""+
							line.substring(Integer.parseInt(balise.substring(2,3)))+"\",\n"+
							tabulation.substring(3+6*val)+"\"content\": {";
							output.write(line2);
							precedent = 1;
							dernierH = Integer.parseInt(balise.substring(2,3));
							indentation= 2*dernierH;
						}
					}
					else if("p".equals(balise.substring(1,2))){
						if(precedent == 1 && "\"p\"".equals(historique.get(historique.size()-1))){
							line2 = "\\n"+line;
							output.write(line2);
						}
						else{
							if(!"h".equals(historique.get(historique.size()-1).substring(1,2))){
								output.write(",");
							}
							line2 = "\n"+tabulation+balise+": \""+line;
							output.write(line2);
							precedent = 1;
						}
					}
					else if("ul".equals(balise.substring(1,3))){
						if(!"h".equals(historique.get(historique.size()-1).substring(1,2))){
							output.write(",");
						}
						output.write("\n"+tabulation+balise+": [\n"+tabulation+"{\"li\": \""+line2.substring(2)+"\"}");
						precedent = 1; 
					}
					else if("code".equals(balise.substring(1,5))){
						if(!"h".equals(historique.get(historique.size()-1).substring(1,2))){
							output.write(",");
						}
						output.write("\n"+tabulation+balise+": \"");
						precedent = 1; 
					}
				}
				else if("".equals(line) && precedent == 1){
					if("ul".equals(historique.get(historique.size()-1).substring(1,3))){
						output.write("]");
					}
					else if(!"h".equals(historique.get(historique.size()-1).substring(1,2))){
						output.write("\"");
					}
					precedent = 0;
				}
				sb.append(line2);      
				sb.append("\n");
				if(balise.length() != 0){
					historique.add(balise);
				}
		    }
			if("ul".equals(historique.get(historique.size()-1).substring(1,3)) && precedent == 1){
				output.write("]");
			}
			else if(!"h".equals(historique.get(historique.size()-1).substring(1,2)) && precedent == 1){
				output.write("\"");
			}
			
			for (int i= 0; i<dernierH; i++){
				output.write("\n"+tabulation.substring(3+6*i)+"}\n"+tabulation.substring(6+6*i)+"}");
			}
				//mettre les retour } ici
			output.write("\n}");
		    output.close();    
		    System.out.println("Contenu du fichier: ");
		    System.out.println(sb.toString());
			
			if(!Desktop.isDesktopSupported()){
				System.out.println("Desktop n'est pas prise en charge");
				return;
			}
			
			Desktop d = Desktop.getDesktop();
			if(fileOut.exists()) 
				d.open(fileOut);
		}
		catch(IOException e){
		    e.printStackTrace();
		}
	}
	
	public static String replace( String old, String actual, String cible){
		String res = "";
		for (int i=0; i<cible.length(); i++){
			if(old.equals(cible.substring(i,i+old.length()))){
				res+=actual;
			}
			else{
				res+=cible.charAt(i);
			}
		}
		return res;
	}
	
	public static String identifierType(String line){
		String res = "";
		if(line.length()>0){
			if("#".equals(line.substring(0,1))){
				res = "\"h1\"";
				if("##".equals(line.substring(0,2))){
					res = "\"h2\"";
					if("###".equals(line.substring(0,3))){
						res = "\"h3\"";
					}
				}
			}
			else if(line.length()>1){
				if("* ".equals(line.substring(0,2))){
					res = "\"ul\"";
				}
				else if(line.length()>4){
					if("```sh".equals(line.substring(0,5))){
						res = "\"code\"";
					}
					else{
						res = "\"p\"";
					}
				}
				else{
					res = "\"p\"";
				}
			}	
		}
		return res;
	}
} 