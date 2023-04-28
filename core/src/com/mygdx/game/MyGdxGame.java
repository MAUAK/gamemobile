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
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture logo;
	private Texture coin;
	private Texture coinatual;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Circle circuloCoin;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float posicaoCoinVertical;
	private float posicaoCoinHorizontal;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int valorMoeda = 5;
	private int valorMoedaOuro = 0;
	private int pontuacaoMaxima = 0;
	private boolean passouCano = false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;

	BitmapFont textoPontucao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;


	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;
	Sound moeda;

	Preferences preferencias;

	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;


	@Override
	public void create() {
		inicializarTexturas();
		inicializaObjetos();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

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
		coin = new Texture("coin.png");
		coinatual = coin;
	}

	private void inicializaObjetos() {
		batch = new SpriteBatch();
		random = new Random();

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		posicaoCoinHorizontal = larguraDispositivo / 2;
		posicaoCoinVertical = posicaoCoinHorizontal + larguraDispositivo / 2;
		espacoEntreCanos = 350;

		textoPontucao = new BitmapFont();
		textoPontucao.setColor(com.badlogic.gdx.graphics.Color.WHITE);
		textoPontucao.getData().setScale(5);

		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(com.badlogic.gdx.graphics.Color.GREEN);
		textoReiniciar.getData().setScale(2);

		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(com.badlogic.gdx.graphics.Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloCoin = new Circle();

		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		preferencias = Gdx.app.getPreferences("FlappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima", 0);

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	private void verificarEstadoJogo() {
		boolean toqueTela = Gdx.input.justTouched();
		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}

		} else if (estadoJogo == 1) {
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}
			posicaoCoinHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;

			if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}
			if (posicaoCoinHorizontal <- coinatual.getWidth() / 2 )
			{
				resetaCoin();
			}
			if (posicaoInicialVerticalPassaro > 0 || toqueTela)
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
			gravidade++;
		} else if (estadoJogo == 2) {
			if (pontos > pontuacaoMaxima) {
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500;


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

		circuloCoin.set(posicaoCoinHorizontal - (coinatual.getWidth()),
		posicaoCoinVertical - (coinatual.getHeight()),
		coinatual.getWidth() / 2);

		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean colidiuCoin = Intersector.overlaps(circuloPassaro, circuloCoin);

		if(colidiuCoin == true){
			if(coinatual == coin){
				pontos+= valorMoeda;
				//else pontos+=valorMoeda2;
				posicaoCoinVertical = alturaDispositivo * 2;
			}
		}

		if (colidiuCanoCima || colidiuCanoBaixo) {
			if (estadoJogo ==1) {
				somColisao.play();
				estadoJogo = 2;
			}
		}
	}

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


		if (estadoJogo == 0)
		{
			batch.draw(logo, larguraDispositivo / 2 - logo.getWidth()/2, alturaDispositivo /2);
			textoReiniciar.draw(batch, "Toque para iniciar!", larguraDispositivo/2 -120, alturaDispositivo /2 - 10);
		}
		if(estadoJogo == 2)
		{
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth()/2, alturaDispositivo /2);
			textoReiniciar.draw(batch, "Toque para reiniciar!", larguraDispositivo/2 -140, alturaDispositivo /2 - gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch,"Seu record é: "+ pontuacaoMaxima+" pontos", larguraDispositivo/2 -140,alturaDispositivo/2 - gameOver.getHeight());
		}
		if (estadoJogo == 1){
			batch.draw(coinatual, posicaoCoinHorizontal - (coinatual.getWidth()),
					posicaoCoinVertical - (coinatual.getWidth()), coinatual.getWidth(),
					coinatual.getHeight());
		}



		batch.end();
	}
	//asmndnoiasd

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

	private void resetaCoin() {
		posicaoCoinHorizontal = posicaoCanoHorizontal + canoBaixo.getWidth() + coinatual.getWidth() +
				random.nextInt((int) (larguraDispositivo - (coinatual.getWidth())));
		posicaoCoinVertical = coinatual.getHeight() / 2 + random.nextInt((int)
				alturaDispositivo - coinatual.getHeight() / 2);

		int randomNewCoin = random.nextInt(100);
		if (randomNewCoin < 30) {
			//coinatual = coin2;

		} else {
			coinatual = coin;
		}
	}

	@Override
	public void resize(int width, int height)
	{viewport.update(width,height);}

	@Override
	public void dispose()
	{

	}
}