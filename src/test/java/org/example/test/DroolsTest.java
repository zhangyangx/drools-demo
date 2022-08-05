package org.example.test;

import org.example.entity.ComparisonOperatorEntity;
import org.example.entity.Order;
import org.example.entity.Student;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yang.zhang 2022/8/5
 */
public class DroolsTest {

    @Test
    public void test1() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
        // 会话对象，用于和规则引擎交互
        KieSession kieSession = kieClasspathContainer.newKieSession();

        // 构造订单对象，设置原始价格，由规则引擎根据优惠规则计算优惠后的价格
        Order order = new Order();
        order.setOriginalPrice(210D);

        // 将数据提供给规则引擎，规则引擎会根据提供的数据进行规则匹配
        kieSession.insert(order);

        // 激活规则引擎，如果规则匹配成功则执行规则
        kieSession.fireAllRules();
        // 关闭会话
        kieSession.dispose();

        System.out.println("优惠前原始价格：" + order.getOriginalPrice() +
                "，优惠后价格：" + order.getRealPrice());
    }

    /**
     * 测试比较操作符
     */
    @Test
    public void test2() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieClasspathContainer.newKieSession();

        ComparisonOperatorEntity comparisonOperatorEntity = new ComparisonOperatorEntity();
        comparisonOperatorEntity.setNames("张三");
        List<String> list = new ArrayList<>();
        list.add("张三");
        list.add("李四");
        comparisonOperatorEntity.setList(list);

        // 将数据提供给规则引擎，规则引擎会根据提供的数据进行规则匹配，如果规则匹配成功则执行规则
        kieSession.insert(comparisonOperatorEntity);

        // 通过规则过滤器实现只执行指定规则
//        kieSession.fireAllRules(new RuleNameEqualsAgendaFilter("rule_comparison_memberOf"));

        kieSession.fireAllRules();
        kieSession.dispose();
    }

    /**
     * 测试Drools提供的内置方法
     */
    @Test
    public void test3() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieClasspathContainer.newKieSession();

        Student student = new Student();
        student.setAge(5);

        // 将数据提供给规则引擎，规则引擎会根据提供的数据进行规则匹配，如果规则匹配成功则执行规则
        kieSession.insert(student);

        kieSession.fireAllRules();
        kieSession.dispose();
    }

    /**
     * 测试agenda-group
     */
    @Test
    public void test4() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieClasspathContainer.newKieSession();

        // 设置焦点，对应agenda-group分组中的规则才可能被触发
        kieSession.getAgenda().getAgendaGroup("myagendagroup_1").setFocus();

        kieSession.fireAllRules();
        kieSession.dispose();
    }

    /**
     * 测试timer属性
     */
    @Test
    public void test5() throws InterruptedException {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
        final KieSession kieSession = kieClasspathContainer.newKieSession();

        //启动规则引擎进行规则匹配，直到调用halt方法才结束规则引擎
        new Thread(kieSession::fireUntilHalt).start();

        Thread.sleep(10000);
        // 结束规则引擎
        kieSession.halt();
        kieSession.dispose();
    }

    /**
     * query查询提供了一种查询working memory中符合约束条件的Fact对象的简单方法。它仅包含规则文件中的LHS部分，不用指定“when”和“then”部分并且以end结束
     */
    @Test
    public void test6() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
        KieSession kieSession = kieClasspathContainer.newKieSession();

        Student student1 = new Student();
        student1.setName("张三");
        student1.setAge(12);

        Student student2 = new Student();
        student2.setName("李四");
        student2.setAge(8);

        Student student3 = new Student();
        student3.setName("王五");
        student3.setAge(22);

        // 将对象插入Working Memory中
        kieSession.insert(student1);
        kieSession.insert(student2);
        kieSession.insert(student3);

        // 调用规则文件中的查询
        QueryResults results1 = kieSession.getQueryResults("query_1");
        int size = results1.size();
        System.out.println("size=" + size);
        for (QueryResultsRow row : results1) {
            Student student = (Student) row.get("$student");
            System.out.println(student);
        }

        System.out.println("================================");

        // 调用规则文件中的查询
        QueryResults results2 = kieSession.getQueryResults("query_2", "王五");
        size = results2.size();
        System.out.println("size=" + size);
        for (QueryResultsRow row : results2) {
            Student student = (Student) row.get("$student");
            System.out.println(student);
        }
        // kieSession.fireAllRules();
        kieSession.dispose();
    }
}
