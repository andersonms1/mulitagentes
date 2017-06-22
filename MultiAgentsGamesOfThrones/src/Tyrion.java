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

		addBehaviour(new TickerBehaviour(this, 10000) {

			@Override
			protected void onTick() {
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

				myAgent.addBehaviour(new RequestAllies());

			}

		});

	}//end setup

	private class RequestAllies extends Behaviour {



		private int aux = 0;
		private MessageTemplate mt;
		private AID aliadoAgent;
		private int repliesCnt = 0;

		public void action(){
			switch(aux){
				case 0:
					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					for (int aux = 0; aux < possibleAllyAgents.length; ++aux){
						cfp.addReceiver(possibleAllyAgents[aux]);
					}
					cfp.setContent(aliado);
					cfp.setConversationId("descobrindo-aliados");
					cfp.setReplyWith("cfp"+System.currentTimeMillis());
					myAgent.send(cfp);

					mt = MessageTemplate.and(MessageTemplate.MatchConversationId("descobrindo-aliados")
					,MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
					aux = 1;
					break;

				case 1:
					ACLMessage reply = myAgent.receive(mt);
					if(reply != null){
							if(reply.getContent() == "YES"){
								//aliado resposta yes
								//informar a rainha de seu novo aliado
								ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
								order.addReceiver(aliadoAgent);
								order.setContent("YES");
								order.setConversationId("descobrindo-aliados");
								order.setReplyWith(""+System.currentTimeMillis());
								myAgent.send(order);//informar rainha
							}else{
								ACLMessage order = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
								order.addReceiver(aliadoAgent);
								order.setContent("NO");
								order.setConversationId("descobrindo-aliados");
								order.setReplyWith(""+System.currentTimeMillis());
								myAgent.send(order);//informar rainha
							}

							repliesCnt++;

					}else{
						block();
					}

			}


		}
		public boolean done(){
			if(repliesCnt > possibleAllyAgents.length)
				return true;
			else
				return false;
		}
	}

}
