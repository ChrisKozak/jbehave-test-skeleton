/**
 *
 */
package com.thoughtworks.core.utils;


import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtworks.core.utils.CoreUtils.getLoggerFor;

/**
 * @author sanad.liaquat
 */
public class HTMLParser {


    /**
     * Parses the provided HTML and returns all nodes
     *
     * @param html
     * @return
     */
    public NodeList getAllNodesFromHTML(String html) {
        Parser parser = null;
        NodeList nodeList = null;
        try {
            parser = new Parser(html);
            nodeList = parser.parse(null);
        } catch (ParserException e) {
            getLoggerFor(this).error(e);
        }
        return nodeList;
    }

    /**
     * Give node list and get node list filtered by class
     *
     * @param htmlClassName
     * @param nodeList
     * @return NodeList
     */
    public NodeList getNodesForClass(String htmlClassName, NodeList nodeList) {
        NodeFilter nodeFilter = new HasAttributeFilter("class", htmlClassName);
        return nodeList.extractAllNodesThatMatch(nodeFilter, true);
    }

    /**
     * Returns the single node that has the given id
     *
     * @param idString
     * @param nodeList
     * @return Node
     */
    public Node getNodeForId(String idString, NodeList nodeList) {
        NodeList filteredNodes = getNodesWithAttributeAndValue("id", idString,
                nodeList);
        Node node = filteredNodes.elementAt(0);
        return node;
    }

    /**
     * Returns all nodes & their child that have the given tag
     *
     * @param tagName
     * @param nodeList
     * @return NodeList
     */
    public NodeList getNodesForTag(String tagName, NodeList nodeList) {
        NodeFilter nodeFilter = new TagNameFilter(tagName);
        return nodeList.extractAllNodesThatMatch(nodeFilter, true);
    }

    /**
     * Returns nodes & their child that have the given attribute
     *
     * @param attrName
     * @param nodeList
     * @return NodeList
     */
    public NodeList getNodesWithAttribute(String attrName, NodeList nodeList) {
        NodeFilter nodeFilter = new HasAttributeFilter(attrName);
        return nodeList.extractAllNodesThatMatch(nodeFilter, true);
    }

    /**
     * Returns nodes & their child that have the given attribute and attribute
     * value
     *
     * @param attrName
     * @param value
     * @param nodeList
     * @return NodeList
     */
    public NodeList getNodesWithAttributeAndValue(String attrName,
                                                  String value, NodeList nodeList) {
        NodeFilter nodeFilter = new HasAttributeFilter(attrName, value);
        return nodeList.extractAllNodesThatMatch(nodeFilter, true);
    }

    /**
     * Returns the text contained in the node
     *
     * @param id
     * @param nodeList
     * @return String
     */
    public String getInnerTextforId(String id, NodeList nodeList) {
        return getNodeText(getNodeForId(id, nodeList));
    }

    /**
     * Returns all links in the given nodeList
     *
     * @param nodeList
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllLinks(NodeList nodeList) {
        nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("a"),
                true).extractAllNodesThatMatch(new HasAttributeFilter("href"));

        ArrayList<String> links = new ArrayList<String>();
        for (int j = 0; j < nodeList.size(); j++) {
            Node linkNode = nodeList.elementAt(j);
            if (linkNode instanceof LinkTag) {
                LinkTag linkTag = (LinkTag) linkNode;
                links.add(linkTag.getLink());
            }
        }
        return links;
    }

      /**
     * Returns all image urls in the given nodeList
     *
     * @param nodeList
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllImageUrls(NodeList nodeList) {
        nodeList = nodeList.extractAllNodesThatMatch(new TagNameFilter("img"),
                true);
        ArrayList<String> imageUrls = new ArrayList<String>();
        for (int j = 0; j < nodeList.size(); j++) {
            Node imgNode = nodeList.elementAt(j);
            if (imgNode instanceof ImageTag) {
                ImageTag imgTag = (ImageTag) imgNode;
                imageUrls.add(imgTag.getImageURL());
            }
        }
        return imageUrls;
    }

    /**
     * Get the inner text of the node
     *
     * @param node
     * @return String
     */
    public String getNodeText(Node node) {
        return node.toPlainTextString().trim().replaceAll("&amp;", "&");
    }

    /**
     * Returns the inner text of the node using the provided regex pattern
     *
     * @param node
     * @param regex
     * @return String
     */
    public String getNodeTextWithPattern(Node node, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(node.toPlainTextString().trim());
        matcher.find();
        return matcher.group();
    }

    /**
     * Give it a node list and it will grab all inner text for each node in
     * nodeList
     *
     * @param nodeList
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllNodeText(NodeList nodeList) {
        ArrayList<String> allNodeText = new ArrayList<String>();
        for (int j = 0; j < nodeList.size(); j++) {
            allNodeText.add(getNodeText(nodeList.elementAt(j)));
        }
        return allNodeText;
    }

    /**
     * Returns the inner text of all the nodes in the given NodeList using the
     * provided regex pattern
     *
     * @param nodeList
     * @param regex
     * @return ArrayList<String>
     */
    public ArrayList<String> getAllNodeTextWithPattern(NodeList nodeList,
                                                       String regex) {
        ArrayList<String> allNodeText = new ArrayList<String>();
        for (int j = 0; j < nodeList.size(); j++) {
            allNodeText
                    .add(getNodeTextWithPattern(nodeList.elementAt(j), regex));
        }
        return allNodeText;
    }

}
