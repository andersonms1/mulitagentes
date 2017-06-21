import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Tyrion extends Agent {

	private AID[] possibleAllyAgents;
	String aliado = "Fire and Blood!?";

	@SuppressWarnings("serial")
	protected void setup() {
		System.out.println("Hello, got some wine?" + getAID().getName() + "is ready.");
		
		/*
		 * Object[] args = getArguments(); if(ar)
		 */

		addBehaviour(new TickerBehaviour(this, 10000) {

			// System.out.println("Procurando aliados.");
			// DFAgentDescription template = new DFAgentDescription();
			// ServiceDescription sd = new ServiceDescription();
			// sd.setType("descobrindo-aliados");
			// template.addServices(sd);

			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("descobrindo-aliados");
				template.addServices(sd);

				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					System.out.println("Found the following allies...");
					possibleAllyAgents = new AID[result.length];
					for (int aux = 0; aux < result.length; ++aux) {
						possibleAllyAgents[aux] = result[aux].getName();
						System.out.println(possibleAllyAgents[aux].getName());
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				//myAgent.addBehaviour(new RequestAllies());

			}

		});

	}//end setup

	private class RequestAllies extends Behaviour {
		public void action(){
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			for (int aux = 0; aux < possibleAllyAgents.length; ++aux){
				cfp.addReceiver(possibleAllyAgents[aux]);
			}
			cfp.setContent(aliado);
			cfp.setConversationId("descobrindo-aliados");
			cfp.setReplyWith("cfp"+System.currentTimeMillis());
			myAgent.send(cfp);
		}
		public boolean done(){
			return false;
		}
	}

}
