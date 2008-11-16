package com.gnipcentral.client;

import com.gnipcentral.client.resource.Filter;
import com.gnipcentral.client.resource.Rule;
import com.gnipcentral.client.resource.RuleType;

import java.util.List;

/**
 * 
 */
public class FilterTestCase extends GnipTestCase {

    public void setUp() throws Exception {
        super.setUp();
        config.setTunnelOverPost(false);
    }

    public void testGetFilter() throws Exception {
        Filter existingFilter = new Filter("existingFilter");
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            gnipConnection.create(localPublisher, existingFilter);

            waitForServerWorkToComplete();

            Filter existing = gnipConnection.getFilter(localPublisher.getName(), existingFilter.getName());

            waitForServerWorkToComplete();

            assertNotNull(existing);
            assertEquals(2, existing.getRules().size());
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            gnipConnection.delete(localPublisher, existingFilter);
        }
    }

    public void testCreateFilter() throws Exception {
        try {
            gnipConnection.create(localPublisher, filterToCreate);

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            assertNotNull(filter);
            assertEquals(filterToCreate.getName(), filter.getName());
            List<Rule> list = filter.getRules();
            assertEquals(1, list.size());
            Rule rule = list.get(0);
            assertEquals(RuleType.ACTOR, rule.getType());
            assertEquals("tom", rule.getValue());
        }
        finally {
            gnipConnection.delete(localPublisher, filterToCreate);
        }
    }

    public void testUpdateFilter() throws Exception {
        try {
            gnipConnection.create(localPublisher, filterToCreate);

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            filter.addRule(new Rule(RuleType.ACTOR, "jojo"));
            gnipConnection.update(localPublisher, filter);

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            List<Rule> rules = updated.getRules();
            assertEquals(2, rules.size());

            int idx = ("jojo".equals(rules.get(0).getValue()) ? 0 : 1);

            assertEquals(RuleType.ACTOR, rules.get(idx).getType());
            assertEquals("jojo", rules.get(idx).getValue());
        }
        finally {
            gnipConnection.delete(localPublisher, filterToCreate);
        }
    }

    public void testDeleteFilter() throws Exception {
        Filter filter = null;
        try {
            gnipConnection.create(localPublisher, filterToCreate);

            waitForServerWorkToComplete();

            filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            assertNotNull(filter);
        }
        finally {
            gnipConnection.delete(localPublisher, filter);
        }

        waitForServerWorkToComplete();

        try {
            gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            fail();
        } catch (GnipException e) {
            //expected
        }
    }

    public void testNoSuchFilter() throws Exception {
        try {
            gnipConnection.getFilter(localPublisher.getName(), "nosuchfilter");
            assertFalse("Should have received exception for missing filter", true);
        }
        catch(GnipException e) {
            // expected
        }
    }

    public void testAddRuleToFilter() throws Exception {
        try {
            gnipConnection.create(localPublisher, filterToCreate);

            waitForServerWorkToComplete();

            Rule ruleToAdd = new Rule(RuleType.ACTOR, "jojo");
            gnipConnection.update(localPublisher, filterToCreate, ruleToAdd);

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            List<Rule> rules = updated.getRules();
            assertEquals(2, rules.size());

            int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;

            assertEquals(RuleType.ACTOR, rules.get(idx).getType());
            assertEquals("jojo", rules.get(idx).getValue());
        }
        finally {
            gnipConnection.delete(localPublisher, filterToCreate);
        }
    }

    public void testDeleteRuleFromFilter() throws Exception {
        try {
            gnipConnection.create(localPublisher, filterToCreate);

            waitForServerWorkToComplete();

            Rule ruleToDelete = new Rule(RuleType.ACTOR, "jojo");
            gnipConnection.update(localPublisher, filterToCreate, ruleToDelete);

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            List<Rule> rules = updated.getRules();
            assertEquals(2, rules.size());

            int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;
            assertEquals(RuleType.ACTOR, rules.get(idx).getType());
            assertEquals("jojo", rules.get(idx).getValue());

            gnipConnection.delete(localPublisher, filterToCreate, ruleToDelete);

            waitForServerWorkToComplete();

            updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            rules = updated.getRules();
            assertEquals(1, rules.size());

            assertEquals(RuleType.ACTOR, rules.get(0).getType());
            assertEquals("tom", rules.get(0).getValue());
        }
        finally {
            gnipConnection.delete(localPublisher, filterToCreate);
        }
    }    
}
