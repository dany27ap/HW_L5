package P1

import kotlinx.cinterop.*
import platform.posix.*
import platform.windows._m_to_int
import sdl.*
import kotlin.math.PI
import kotlin.math.cos

fun get_SDL_Error() = SDL_GetError()!!.toKString()

class SDL_Visualizer(val width: Int, val height: Int): GameFieldVisualizer, UserInput {
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private val windowX: Int
    private val windowY: Int
    private val window: CPointer<SDL_Window>
    private val renderer: CPointer<SDL_Renderer>

    init {
        if (SDL_Init(SDL_INIT_EVERYTHING) != 0) {
            println("SDL_Init Error: ${get_SDL_Error()}")
            throw Error()
        }

        memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            if (SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()) != 0) {
                println("SDL_GetCurrentDisplayMode Error: ${get_SDL_Error()}")
                SDL_Quit()
                throw Error()
            }
            displayWidth = displayMode.w
            displayHeight = displayMode.h
        }

        windowX = (displayWidth - width) / 2
        windowY = (displayHeight - height) / 2

        val window = SDL_CreateWindow("P1 PP Lab4", windowX, windowY, width, height, SDL_WINDOW_SHOWN)
        if (window == null) {
            println("SDL_CreateWindow Error: ${get_SDL_Error()}")
            SDL_Quit()
            throw Error()
        }

        this.window = window

        val renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC)
        if (renderer == null) {
            SDL_DestroyWindow(window)
            println("SDL_CreateRenderer Error: ${get_SDL_Error()}")
            SDL_Quit()
            throw Error()
        }
        this.renderer = renderer

        SDL_PumpEvents()
        SDL_SetWindowSize(window, width, height)
    }

    override fun refresh() {
        SDL_RenderClear(renderer)
        drawSomething()
        SDL_RenderPresent(renderer)
    }

    private fun drawSomething() {
        SDL_SetRenderDrawColor(renderer, 255, 255, 0, SDL_ALPHA_OPAQUE.toUByte())
        val shape: SDL_Rect
        val arena = Arena()
        shape = arena.alloc<SDL_Rect>()
        shape.w = 50
        shape.y = 50
        shape.w = 100
        shape.h = 100
        //fillRect(shape)
        Circle(width / 2,height / 2, if (width < height) width / 4 else height / 4)
        SDL_SetRenderDrawColor(renderer, 0, 0, 0, SDL_ALPHA_OPAQUE.toUByte())
    }

    private fun fillRect(rect: SDL_Rect) {
        memScoped {
            SDL_RenderFillRect(renderer, rect.ptr.reinterpret())
        }
    }

    private fun Circle(x : Int, y : Int, r : Int)
    {
        var alfa = 0.0

        var sx1  = (r * cos(alfa)).toInt()
        var sy1  = (r * sin(alfa)).toInt()

        for (i in 0..1000)
        {
            alfa += 360/1000.0
            val sx2 = (r * cos(alfa)).toInt()
            val sy2 = (r * sin(alfa)).toInt()

            SDL_RenderDrawLine(renderer, x + sx1, y + sy1,x + sx2,y + sy2 )

            sx1 = sx2
            sy1 = sy2
        }
    }

    override fun readCommands(): List<UserCommand> {
        val commands = mutableListOf<UserCommand>()
        memScoped {
            val event = alloc<SDL_Event>()
            while (SDL_PollEvent(event.ptr.reinterpret()) != 0) {
                val eventType = event.type
                when (eventType) {
                    SDL_QUIT -> commands.add(UserCommand.EXIT)
                    SDL_KEYDOWN -> {
                        val keyboardEvent = event.ptr.reinterpret<SDL_KeyboardEvent>().pointed
                        when (keyboardEvent.keysym.scancode) {
                            SDL_SCANCODE_LEFT -> commands.add(UserCommand.LEFT)
                            SDL_SCANCODE_RIGHT -> commands.add(UserCommand.RIGHT)
                            SDL_SCANCODE_DOWN -> commands.add(UserCommand.DOWN)
                            SDL_SCANCODE_Z, SDL_SCANCODE_SPACE -> commands.add(UserCommand.ACTION)
                            SDL_SCANCODE_UP -> commands.add(UserCommand.UP)
                            SDL_SCANCODE_ESCAPE -> commands.add(UserCommand.EXIT)
                        }
                    }
                    SDL_MOUSEBUTTONDOWN -> {
                        val mouseEvent = event.ptr.reinterpret<SDL_MouseButtonEvent>().pointed
                        val x = mouseEvent.x
                        val y = mouseEvent.y
                        print("x = $x")
                        print("y = $y")
                    }
                }
            }
        }
        return commands
    }

    fun destroy() {
        SDL_DestroyRenderer(renderer)
        SDL_DestroyWindow(window)
        SDL_Quit()
    }
}
