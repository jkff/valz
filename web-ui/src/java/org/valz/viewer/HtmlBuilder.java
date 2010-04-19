package org.valz.viewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on: 28.03.2010 11:10:30
 */
class HtmlBuilder {
    private String tag;
    private String text;
    private List<String> attNames = new ArrayList<String>();
    private List<String> attValues = new ArrayList<String>();
    private List<HtmlBuilder> children = new ArrayList<HtmlBuilder>();

    private HtmlBuilder() {
    }

    public static HtmlBuilder html(String tag, Object... atts) {
        HtmlBuilder res = new HtmlBuilder();
        res.tag = tag;
        for (int i = 0; i < atts.length; i += 2) {
            res.attNames.add(atts[i].toString());
            res.attValues.add(atts[i + 1].toString());
        }
        return res;
    }

    public static HtmlBuilder text(Object text) {
        HtmlBuilder res = new HtmlBuilder();
        res.text = String.valueOf(text);
        return res;
    }

    public void addChild(HtmlBuilder child) {
        children.add(child);
    }

    public HtmlBuilder children(HtmlBuilder... children) {
        for (HtmlBuilder child : children) addChild(child);
        return this;
    }

    public StringBuilder toString(StringBuilder sb) {
        if (tag != null) {
            sb.append("<" + tag + " ");
            for (int i = 0; i < attNames.size(); ++i)
                sb.append(attNames.get(i) + "=\"" + attValues.get(i) + "\"");
            sb.append(">\n");
            for (HtmlBuilder c : children) c.toString(sb);
            sb.append("</" + tag + ">");
        } else {
            sb.append(text);
        }
        return sb;
    }

}
