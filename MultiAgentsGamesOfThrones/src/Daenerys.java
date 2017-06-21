import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Daenerys extends Agent {

	class contactAlliesBehaviour extends Behaviour {

		public boolean recebido = false;

		public contactAlliesBehaviour(Agent agent) {
			super(agent);
		}


		public void action() {

			System.out.println("Inicializando a action do meu comportamneto!");
			System.out.println("Recebendo o conte�do enviado por um terceiro!");

			ACLMessage agentMenssage = myAgent.receive();


			if(agentMenssage != null){

				String person = agentMenssage.getContent();
				Boolean trustful = person.equals("t");
				ACLMessage reply = agentMenssage.createReply();

				if (trustful == true) {

					//agente Tyrion(ainda nao implementado), deve mandar a lista dos amigos
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("Adicionado aos alidados");
					System.out.println("Mensagem recebida, agente confiavel !");
					recebido = true;


				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("Não adicionado aos aliados");
					System.out.println("Mensagem recebida, agente n�o confiavel");
					recebido = true;
				}

				send(reply);


			}else{

				block();
			}


		}// fim action

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return recebido;
		}

	} // Fim da classe makingFriendsBehaviour

	// M�todo setup(), obrigat�rio nos agentes implementados em JADE
	protected void setup() {

		System.out.println("Eu sou seu primeiro agente e quero conversar com outros agentes!");

		// Registrando o agente no DF
		System.out.println("Estou me registrando no DF!!");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Daenerys");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);

			// Criando o behaviour
			System.out.println("Criando e adicionando um comportamento!");
			contactAlliesBehaviour mFB = new contactAlliesBehaviour(this);
			addBehaviour(mFB);

		} catch (FIPAException e) {
			e.printStackTrace();
			doDelete();
		}
	} // fim do m�todo setup

	// m�todo takeDown
	protected void takeDown() {
		// Retira registo no DF
		try {
			System.out.println("At� a pr�xima!");
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	} // fim da classe MyInitialAgent

}
