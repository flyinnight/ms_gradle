package com.dilapp.radar.ui.skintest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.dilapp.radar.R;
import com.dilapp.radar.textbuilder.utils.JsonUtils;

/**
 * 皮肤数值等级标准
 */
public class SkinLevelRulesHelper {

    private Context context;
    private Map<String, List<SkinRuleDiff>> rulesContent = new HashMap<String, List<SkinRuleDiff>>();
    private Map<String, List<SkinRuleDiff>> diffsContent = new HashMap<String, List<SkinRuleDiff>>();

    public SkinLevelRulesHelper(Context context) {
        this.context = context;
        long s = System.currentTimeMillis();
        parser();
        long e = System.currentTimeMillis();
        android.util.Log.i("III", (e - s) + "mils");
    }

    public String getEvaluation(String type, int val) {
        SkinRuleDiff rule = getSkinRule(rulesContent, type, val);
        if(rule == null) {
            return null;
        }
        return rule.evaluation;
    }

    public String getDescription(String type, int val) {
        SkinRuleDiff rule = getSkinRule(rulesContent, type, val);
        if(rule == null) {
            return null;
        }
        return rule.description;
    }

    public String getDiffEvaluation(String type, int val) {
        SkinRuleDiff rule = getSkinRule(diffsContent, type, val);
        if(rule == null) {
            return null;
        }
        return rule.evaluation;
    }

    public SkinRuleDiff[] getSkinRules(String type) {
        return rulesContent.get(type).toArray(new SkinRuleDiff[0]);
    }

    public SkinRuleDiff[] getSkinDiffs(String type) {
        return diffsContent.get(type).toArray(new SkinRuleDiff[0]);
    }

    private SkinRuleDiff getSkinRule(Map<String, List<SkinRuleDiff>> content, String type, int val) {
        List<SkinRuleDiff> rules = content.get(type);
        if(rules == null || rules.size() == 0) {
            return null;
        }
        SkinRuleDiff result = null;
        for (SkinRuleDiff rule : rules) {
            if(rule == null) {
                continue;
            }
            if(val <= Math.max(rule.min, rule.max) && val >= Math.min(rule.min, rule.max)) {
                result = rule;
                break;
            }
        }
        return result;
    }

    private void parser() {

        final String namespace = null;
        XmlResourceParser parser = context.getResources().getXml(R.xml.skin_level_rules);

        try {
            int eventType = parser.getEventType();
            List<SkinRuleDiff> rules = null;
            List<SkinRuleDiff> diffs = null;
            String name = null;
            SkinRuleDiff rule = null;
            SkinRuleDiff diff = null;
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                String tag = parser.getName();
                switch (eventType) {
                    case XmlResourceParser.START_TAG: {
                        if ("Type".equals(tag)) {
                            rules = new ArrayList<SkinRuleDiff>();
                            diffs = new ArrayList<SkinRuleDiff>();
                            name = getAttributeString(parser, namespace, "name");
                        } else if("Rule".equals(tag)) {
                            rule = new SkinRuleDiff();
                            set(parser, namespace, rule);
                        } else if("Diff".equals(tag)) {
                            diff = new SkinRuleDiff();
                            set(parser, namespace, diff);
                        }
                        break;
                    }
                    case XmlResourceParser.END_TAG: {
                        if ("Type".equals(tag)) {
                            rulesContent.put(name, rules);
                            diffsContent.put(name, diffs);
                            // android.util.Log.i("III", name);
                            name = null;
                            rules = null;
                            diffs = null;
                        } else if("Rule".equals(tag)) {
                            rules.add(rule);
                            // android.util.Log.i("III", "\t" + rule.toString());
                            rule = null;
                        } else if("Diff".equals(tag)) {
                            // android.util.Log.i("III", "\t" + diff.toString());
                            diffs.add(diff);
                            diff = null;
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parser.close();
    }

    private void set(XmlResourceParser parser, String namespace, SkinRuleDiff rd) {
        rd.level = getAttributeString(parser, namespace, "level");
        if(rd.level != null) {
            String[] level = rd.level.split("~");
            rd.min = Integer.parseInt(level[0]);
            rd.max = Integer.parseInt(level[1]);
        }
        rd.evaluation = getAttributeString(parser, namespace, "evaluation");
        rd.description = getAttributeString(parser, namespace, "description");
    }

    private String getAttributeString(XmlResourceParser parser, String namespace, String name) {
        int res = parser.getAttributeResourceValue(namespace, name, 0);
        if(res != 0) {
            return context.getString(res);
        }
        return parser.getAttributeValue(namespace, name);
    }

    class SkinRuleDiff {
        private int min;
        private int max;
        private String level;
        private String evaluation;
        private String description;

        public String getDescription() {
            return description;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public String getLevel() {
            return level;
        }

        public String getEvaluation() {
            return evaluation;
        }

        @Override
        public String toString() {
            return JsonUtils.toJson(this);
        }
    }
}
