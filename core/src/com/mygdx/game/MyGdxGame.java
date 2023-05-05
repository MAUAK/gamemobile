package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	//Criando variáveis de textura
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture logo;
	private Texture coin1;
	private Texture coin2;
	private Texture coinatual;

	//Criando variáveis de colisão
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Circle circuloCoin1;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	//Criando variáveis de posições
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float posicaoCoin1Vertical;
	private float posicaoCoin1Horizontal;

	//Criando variáves do coletável e do pássaro
	private float escalaCoin = 1f;
	private float escalaMario = 1f;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int valorCoin1 = 10;
	private int valorCoin2 = 5;
	private int pontuacaoMaxima = 0;
	private boolean passouCano = false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;

	//Criando variável de texto
	BitmapFont textoPontucao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	//Criando variável de som
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound somCoin;

	Preferences preferencias;

	//Criando variáveis para as dimensões da tela
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;

	//reescrevendo o método para iniciar as texturas e objetos
	@Override
	public void create() {
		inicializarTexturas();
		inicializaObjetos();
	}

	//reescrevendo o método para iniciar os métodos restantes
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	//Criando método para iniciar as texturas do pássaro, fundo, cano, tela de fim de jogo e moedas
	private void inicializarTexturas() {
		passaros = new Texture[3];
		passaros[0] = new Texture("mario1.png");
		passaros[1] = new Texture("mario2.png");
		passaros[2] = new Texture("mario3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		logo = new Texture("logoMario.png");
		coin1 = new Texture("coin.png");
		coin2 = new Texture("coin2.png");
		//coinatual = coin1;
		coinatual = coin2;
	}

	//Criando método para iniciar os objetos
	private void inicializaObjetos() {
		batch = new SpriteBatch();
		random = new Random();

		//Declarando as variáveis de posição dos canos
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		posicaoCoin1Horizontal = posicaoCoin1Horizontal + larguraDispositivo / 2;
		posicaoCoin1Vertical = alturaDispositivo/2;
		espacoEntreCanos = 350;

		//Declarando as propriedades dos textos de pontuação
		textoPontucao = new BitmapFont();
		textoPontucao.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		textoPontucao.getData().setScale(5);

		//Declarando as propriedades dos textos de reiniciar
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(com.badlogic.gdx.graphics.Color.GREEN);
		textoReiniciar.getData().setScale(2);

		//Declarando as propriedades dos textos de melhor pontuação
		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(com.badlogic.gdx.graphics.Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		//Declarando as formas de colisão dos objetos do jogo
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloCoin1 = new Circle();

		//Declarando os sons das variáveis de som
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));
		somCoin = Gdx.audio.newSound(Gdx.files.internal("smw_coin.wav"));

		//Declarando as propriedades da variáveis de preferências e pontuação
		preferencias = Gdx.app.getPreferences("FlappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

		//Declarando as propriedades da câmera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	//Criando método para verificar os estados do jogo
	private void verificarEstadoJogo() {
		boolean toqueTela = Gdx.input.justTouched();
		//Verificando se o estado do jogo for antes de iniciar e o jogador tocar na tela, começa o jogo
		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
			//Verificando se o estado do jogo for depois que iniciar, aparece os canos
		} else if (estadoJogo == 1) {
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			posicaoCoin1Horizontal -= Gdx.graphics.getDeltaTime() * 200;
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;

			//ramdomizando a posição dos canos
			if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			//Se passou pelas moedas, chama o método de resetar a moeda
			if (posicaoCoin1Horizontal <- coinatual.getWidth() / 2 * escalaCoin)
			{
				resetaCoin();
			}
			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;
			//Se o player morrer, computa a pontuação máxima
		} else if (estadoJogo == 2) {
			if (pontos > pontuacaoMaxima) {
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;

			//Se tocar na tela, reiniciar a fase e os atributos
			if (toqueTela) {
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo / 2;
				posicaoCanoHorizontal = larguraDispositivo;
				resetaCoin();
			}
		}
	}

	//Criando método para detectar colisão dos objetos
	private void detectarColisoes()
	{
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros[0].getWidth() / 2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2);

		retanguloCanoBaixo.set(
				posicaoCanoHorizontal, alturaDispositivo /2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight());

		circuloCoin1.set(posicaoCoin1Horizontal - ((coinatual.getWidth() * escalaCoin) / 2),
		posicaoCoin1Vertical - ((coinatual.getHeight() * 2) / 2),
				(coinatual.getWidth() * escalaCoin) / 2);

		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean colidiuCoin1 = Intersector.overlaps(circuloPassaro, circuloCoin1);

		//Verifiando se colidiu com qual moeda para dar o devido valor
		if(colidiuCoin1 == true){
			if(coinatual == coin1) pontos += valorCoin1;
			else pontos += valorCoin2;
			posicaoCoin1Vertical = alturaDispositivo * 2;
			somCoin.play();
		}

		//Se colidiu com o cano, muda de estado
		if (colidiuCanoCima || colidiuCanoBaixo) {
			if (estadoJogo ==1) {
				somColisao.play();
				estadoJogo = 2;
			}
		}
	}

	//Criando métodoso para desenhar as texturas dos objetos
	private void desenharTexturas()
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(fundo, 0 , 0 , larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[(int) variacao],
				50 + posicaoHorizontalPassaro,posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo,
				posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);
		textoPontucao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2, alturaDispositivo -110);

		//Se o estado do jogo for antes de iniciar, aparece a logo e o texto de iniciar
		if (estadoJogo == 0)
		{
			batch.draw(logo, larguraDispositivo / 2 - logo.getWidth()/2, alturaDispositivo /2);
			textoReiniciar.draw(batch, "Toque para iniciar!", larguraDispositivo/2 -120, alturaDispositivo /2 - 10);
		}
		//Se o estado do jogo for depois de morrer, aparece a tela de game over o texto de reiniciar
		if(estadoJogo == 2)
		{
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth()/2, alturaDispositivo /2);
			textoReiniciar.draw(batch, "Toque para reiniciar!", larguraDispositivo/2 -140, alturaDispositivo /2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,"Seu record é: "+ pontuacaoMaxima+" pontos", larguraDispositivo/2 -140,alturaDispositivo/2 - gameOver.getHeight());
		}
		//Se o estado do jogo for durante a gameplay, aparece as moedas
		if (estadoJogo == 1){
			batch.draw(coinatual, posicaoCoin1Horizontal - (coinatual.getWidth() * escalaCoin),
					posicaoCoin1Vertical - (coinatual.getWidth() * escalaCoin),
					coinatual.getWidth() * escalaCoin,
					coinatual.getHeight() *escalaCoin);
		}
		batch.end();
	}

	//Criando método para validar ponto
	private void validarPontos()
	{
		if(posicaoCanoHorizontal < 50-passaros[0].getWidth())
		{
			if(!passouCano)
			{
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}
		variacao += Gdx.graphics.getDeltaTime()*10;
		if(variacao > 3)
			variacao = 0;
	}

	//Criando método para resetar a moeda assim que pegar a anterior
	private void resetaCoin() {
		posicaoCoin1Horizontal = posicaoCanoHorizontal + canoBaixo.getWidth() + coinatual.getWidth() +
				random.nextInt((int) (larguraDispositivo - (coinatual.getWidth() * escalaCoin)));
		posicaoCoin1Vertical = coinatual.getHeight() / 2 + random.nextInt((int)
				alturaDispositivo - coinatual.getHeight() / 2);

		int randomNewCoin = random.nextInt(100);
		if (randomNewCoin < 30) {
			coinatual = coin2;
		} else {
			coinatual = coin1;
		}
	}

	//Reescrevendo o método da dimensão da tela
	@Override
	public void resize(int width, int height)
	{viewport.update(width,height);}

	@Override
	public void dispose()
	{

	}
}