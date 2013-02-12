package leveledit;
import java.io.FileInputStream;
/**
 * Reads text files with different posts. Used for inifile & RILedit internal file format.
 * <p>The file format:
 * 
 * <br>Posts are marked with [name]
 * <br>comments with #
 * <br>you can jump to post x, then parse word 1,2...
 * <br>or parse line 1,2...
 * 
 * <p>TODO: Ok, it's very quick and dirty but it works...
 * 
 */
public class ResFileReader {

    private StringBuffer file = new StringBuffer();
    protected StringBuffer word = new StringBuffer(); // the selected word
    private int pos = 0; // file char pos


    /**
     * Opens a file to read
     * @param path
     * @throws java.lang.Exception
     */
    public ResFileReader (String path) throws Exception 
    {
	try {
	    if (path == null) throw new Exception();

	    Class c = this.getClass();
	    FileInputStream is = new FileInputStream(path);
	    byte b[] = new byte[1];

	    int fas = 0;
	    while ( is.read(b) != -1 ) {
		file.append(new String(b));
	    }
	    
	    is.close();	   
	    nextWord();
	}
	catch (Exception e) {
	    throw new Exception("Couldn't find " + path);
	}
    }
     
    /**
     * 
     * @return
     */
    public String getWord() { return word.toString(); }

    /**
     * 
     * @return
     * @throws java.lang.Exception
     */
    public String getNextWord() throws Exception
    { 
	if (!nextWord()) throw new Exception("getNextWord: end of line"); 
	return getWord(); 
    }

    /**
     * 
     * @return
     * @throws java.lang.Exception
     */
    public int getNextWordAsInt() throws Exception
    { 
	if (!nextWord()) throw new Exception("getNextWordAsInt: end of line"); 
	return getWordAsInt(); 
    }

    /**
     * 
     * @return
     * @throws java.lang.Exception
     */
    public boolean getNextWordAsBool() throws Exception
    { 
	if (!nextWord()) throw new Exception("getNextWordAsBool: end of line"); 
	return getWordAsBool(); 
    }

    /**
     * 
     * @return
     * @throws java.lang.Exception
     */
    public int getWordAsInt() throws Exception
    { 
	try {
	    return Integer.parseInt(getWord());
	}
	catch (Exception e) {throw new Exception("getWordAsInt: Word wasn't an int, found: " + getWord()) ;}
    }

    /**
     * 
     * @return
     * @throws java.lang.Exception
     */
    public boolean getWordAsBool() throws Exception
    { 
	if (getWord().compareTo("YES") == 0) return true;
	if (getWord().compareTo("NO") == 0 ) return false;
	throw new Exception("getWordAsBool: Wasn't YES or NO; Found: " + getWord()) ;
    }

    /**
     * 
     * @return
     */
    public String getRestOfLine()
    {
	StringBuffer s = new StringBuffer();
	try {
	    while(true) 
		s.append(getNextWord() + " ");
	} catch (Exception e) 
	    {
		return s.toString();
	    }

    }

    /**
     * gets the char that pos is selecting
     * @return
     * @throws java.lang.Exception
     */
    private char getChar() throws Exception
    {
	if (pos < file.length())
	    return file.charAt(pos);

	throw new Exception("eof");
    }

    /**
     * Go to next char, false if eof
     * @return
     */
    private boolean nextChar()
    {
	if (pos >= file.length())
	    return false;
	pos++;
	return true;
    }
    
    /**
     * Go to post [name]
     * @param name  Post/chunk name
     * @param readFirstLine Should first line be read
     * @throws java.lang.Exception
     */
    public void gotoPost(String name, boolean readFirstLine) throws Exception
    {
	pos = 0;

	while (true) {
	    
	    try {
		if (getChar() == '[') {
		    StringBuffer s = new StringBuffer();
		    // found a post, is it the right one?
		    nextChar();
		    while (getChar() != ']'){
			s.append(getChar());
			nextChar();
		    }
		    if (s.toString().compareTo(name) == 0) {
			// ok, it was found
			if (readFirstLine) nextLine();
			return;
		    }
		} 
	    } catch (Exception e) {}	


	    // didn't find, next row
	    if (nextChar() == false) throw new Exception("Couldn't find post " + name);
	}
    } 

    /**
     * Go to post
     * @param name
     * @throws java.lang.Exception
     */
    
    public void gotoPost(String name) throws Exception
    {
	try {

	    gotoPost(name,true);

	}catch (Exception e) {}
    }
    
    /**
     * Go to next line & skip tab/whitespace
     * @param readFirstWord Should first word be read
     * @return false if eof
     */
    public boolean nextLine(boolean readFirstWord)
    {
	while (true) {
	    try {
		// goto end of line
		while (getChar() != '\n' && getChar() != '\r')
		    if (nextChar() == false) return false; // eof.

		if (readFirstWord) {

		    // get first char of line
		    if (nextChar() == false) return false; // eof.

		    // get char not tab or whitespace
		    while (getChar() == ' ' || getChar() == '\t') 
			if (nextChar() == false) return false; // eof

		    // check not comment or end of line
		    if (getChar() != '\n' && getChar() != '\r' && getChar() != '#') {
			nextWord();
			return true;
		    }

		} else return true;
	    } catch (Exception e) { return false; } // eof
	}	
    }

    /**
     * 
     * @see #nextLine(boolean)
     * @return false if eof
     */
    public boolean nextLine(){
	return nextLine(true);
    }
    
    /**
     * Read next word to the wordbuffer
     * @return false if end of line or file
     */
    public boolean nextWord()
    {
	try {
	    // check that not end of line or comment
	    if (getChar() == '\n' || getChar() == '\r' || getChar() == '#') return false;

	    // goto next char not tab or whitespace
	    while (getChar() == ' ' || getChar() == '\t') nextChar();

	    // flush buffer
	    word = new StringBuffer();

	    // red to next whitespace, tab, end of line or EOF
	    while (getChar() != ' ' &&  getChar() != '\t' &&
		   getChar() != '\n' && getChar() != '\r' && getChar() != '#') {
		word.append(getChar());
		nextChar();
	    }

	    // goto next char not tab or whitespace
	    while (getChar() == ' ' || getChar() == '\t')
		if (nextChar() == false) return false; // eof.
	    
	} catch (Exception e) { return false; } // eof
	return true;
    }
    
    /**
     * Read a map array in current post.
     * Format is asciicoded integers separated by whitespace.
     */
    public int [][] readMapArray() {
       
        int [][] map = new int[1][1]; 
        
	try {	    
	    nextLine(true);
	    int x = (int)getWordAsInt();
	    int y = (int)getNextWordAsInt();	    

	    nextLine(true);

	    map = new int [y][x];
	    
	    for (int i = 0; i < y; i++) {
		for (int j = 0; j < x; j++) {
		   
		    if (j==0 && i==0) map[i][j] = getWordAsInt();
		    else map[i][j] = getNextWordAsInt();

		}
	    }
	} catch (Exception e){ 
            System.out.println("Error in ResFileReader.readMapArray()"); 
        }

	return map;
    }
}