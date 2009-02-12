package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;

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
        boolean failed = false;
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            Result result = gnipConnection.create(localPublisher, existingFilter);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter existing = gnipConnection.getFilter(localPublisher, existingFilter.getName());

            waitForServerWorkToComplete();

            assertNotNull(existing);
            assertEquals(2, existing.getRules().size());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, existingFilter);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testCreateFilter() throws Exception {
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, filterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            assertNotNull(filter);
            assertEquals(filterToCreate.getName(), filter.getName());
            List<Rule> list = filter.getRules();
            assertEquals(1, list.size());
            Rule rule = list.get(0);
            assertEquals(RuleType.ACTOR, rule.getType());
            assertEquals("tom", rule.getValue());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, filterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testUpdateFilter() throws Exception {
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, filterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            filter.addRule(new Rule(RuleType.ACTOR, "jojo"));
            result = gnipConnection.update(localPublisher, filter);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            List<Rule> rules = updated.getRules();
            assertEquals(2, rules.size());

            int idx = ("jojo".equals(rules.get(0).getValue()) ? 0 : 1);

            assertEquals(RuleType.ACTOR, rules.get(idx).getType());
            assertEquals("jojo", rules.get(idx).getValue());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, filterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testDeleteFilter() throws Exception {
        boolean failed = false;
        Filter filter = null;
        try {
            Result result = gnipConnection.create(localPublisher, filterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            filter = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            assertNotNull(filter);
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, filter);
            assertTrue(result.isSuccess() || failed);
        }

        waitForServerWorkToComplete();

        try {
            gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            fail();
        } catch (GnipException e) {
            //expected
        }
    }

    public void testNoSuchFilter() throws Exception {
        try {
            gnipConnection.getFilter(localPublisher, "nosuchfilter");
            assertFalse("Should have received exception for missing filter", true);
        }
        catch(GnipException e) {
            // expected
        }
    }

    public void testAddRuleToFilter() throws Exception {
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, filterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Rule ruleToAdd = new Rule(RuleType.ACTOR, "jojo");
            result = gnipConnection.update(localPublisher, filterToCreate, ruleToAdd);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            List<Rule> rules = updated.getRules();
            assertEquals(2, rules.size());

            int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;

            assertEquals(RuleType.ACTOR, rules.get(idx).getType());
            assertEquals("jojo", rules.get(idx).getValue());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, filterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testBatchAddRulesToFilter() throws Exception {
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, filterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Rule r1 = new Rule(RuleType.ACTOR, "jojo"),
                 r2 = new Rule(RuleType.ACTOR, "moe"),
                 r3 = new Rule(RuleType.ACTOR, "barney");                                        

            Rules rules = new Rules();
            rules.add(r1).add(r2).add(r3);

            result = gnipConnection.update(localPublisher, filterToCreate, rules);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            List<Rule> updatedRules = updated.getRules();
            assertEquals(4, updatedRules.size());
            assertTrue(updatedRules.contains(r1));
            assertTrue(updatedRules.contains(r2));
            assertTrue(updatedRules.contains(r3));
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, filterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testDeleteRuleFromFilter() throws Exception {
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, filterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Rule ruleToDelete = new Rule(RuleType.ACTOR, "jojo");
            result = gnipConnection.update(localPublisher, filterToCreate, ruleToDelete);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter updated = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            List<Rule> rules = updated.getRules();
            assertEquals(2, rules.size());

            int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;
            assertEquals(RuleType.ACTOR, rules.get(idx).getType());
            assertEquals("jojo", rules.get(idx).getValue());

            result = gnipConnection.delete(localPublisher, filterToCreate, ruleToDelete);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            updated = gnipConnection.getFilter(localPublisher, filterToCreate.getName());
            rules = updated.getRules();
            assertEquals(1, rules.size());

            assertEquals(RuleType.ACTOR, rules.get(0).getType());
            assertEquals("tom", rules.get(0).getValue());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, filterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }    
}
