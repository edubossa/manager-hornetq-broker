manager-hornetq-broker
======================

Gerenciador administrativo do Horneqt, o qual disponibiliza as informações da Fila, em tempo de execução em forma de graficos, fornecendo funcionalidades de visualização e delete, etc...

=====================
OBS: sera necessario adicionar a seguinte configuracao no arquivo: 

hornetq-configuration.xml


    <management-address>jms.queue.hornetq.management</management-address>
  
    <security-settings>
      <!--security for management queue-->
      <security-setting match="jms.queue.hornetq.management">
      <permission type="manage" roles="guest" />
    </security-setting>
    
    </security-settings>


Veja o Layout do projeto <a href='https://drive.google.com/file/d/0B8q_GPBeJwmybW1mS0hvTVBUVUU/edit?usp=sharing'> aqui...</a>








