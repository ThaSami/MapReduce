package com.atypon.workflow;

import com.atypon.workflow.phase.Executor;
import com.atypon.workflow.phase.Phase;
import com.atypon.workflow.phase.PhaseImpl;
import com.atypon.workflow.phase.Rollback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class XmlWorkflowParser implements WorkflowParser {

  @Override
  public Workflow parse(String data) {
    Workflow workflow = new WorkflowImp();
    List<Phase> phases = createPhases(data);
    phases.forEach(workflow::addPhase);
    return workflow;
  }

  private List<Phase> createPhases(String data) {
    List<Phase> phases = new ArrayList<>();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputStream inputStream = new ByteArrayInputStream(data.getBytes());
      Document doc = builder.parse(inputStream);
      doc.getDocumentElement().normalize();
      XPath xPath = XPathFactory.newInstance().newXPath();

      String expression = "/workflow/phase";
      NodeList nodeList =
              (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node nNode = nodeList.item(i);
        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) nNode;
          Class<?> klass =
                  Class.forName(element.getElementsByTagName("executor").item(0).getTextContent());
          Constructor<?> constructor = klass.getConstructor();
          Executor exec = (Executor) constructor.newInstance();

          klass = Class.forName(element.getElementsByTagName("rollback").item(0).getTextContent());
          Method factoryMethod = klass.getDeclaredMethod("getInstance");
          Rollback rollbak = (Rollback) factoryMethod.invoke(null, null);
          phases.add(new PhaseImpl(exec, rollbak));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return phases;
  }
}
