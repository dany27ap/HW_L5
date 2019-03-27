package P2

import kotlinx.cinterop.*
import sdl.*



fun get_SDL_Error() = SDL_GetError()!!.toKString()
var op : Char = '\u0000'

fun Handler_button(root : GObject, caracter : Char)
{
    val edit : GEdit = root.FindObject(5000) as GEdit
    edit.AddChar(caracter)
}

fun Handler_eq(root : GObject)
{
    val edit : GEdit = root.FindObject(5000) as GEdit
    var rezultat = edit.GetText()
    var rez : Double = rezultat.eval()
    edit.SetText(rez.toString())
}



fun main(args: Array<String>) {
    var xor_click = 0
    var quit : Boolean = false

    val width = 320
    val height = 320

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
    }

    val window = SDL_CreateWindow("P2 PP Lab4 - Calc", SDL_WINDOWPOS_UNDEFINED.toInt(), SDL_WINDOWPOS_UNDEFINED.toInt(), width, height, SDL_WINDOW_SHOWN)
    if (window == null) {
        println("SDL_CreateWindow Error: ${get_SDL_Error()}")
        SDL_Quit()
        throw Error()
    }

    val renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC)
    if (renderer == null) {
        SDL_DestroyWindow(window)
        println("SDL_CreateRenderer Error: ${get_SDL_Error()}")
        SDL_Quit()
        throw Error()
    }

    SDL_SetWindowSize(window, width, height)
    SDL_PumpEvents()



    val Panel = GPanel(renderer, 1000, 10, 10, 300, 300, Color(128U,255U,255U))
    val B0 : GObject = GButton(renderer, 2000, "0", 80, 230, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '0') } , '0')
    val B1 : GObject = GButton(renderer, 2001, "1", 30, 80, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '1') }, '1')
    val B2 : GObject = GButton(renderer, 2002, "2", 80, 80, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '2') }, '2')
    val B3 : GObject = GButton(renderer, 2003, "3", 130, 80, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '3') }, '3')
    val B4 : GObject = GButton(renderer, 2004, "4", 30, 130, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '4') }, '4')
    val B5 : GObject = GButton(renderer, 2005, "5", 80, 130, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '5') }, '5')
    val B6 : GObject = GButton(renderer, 2006, "6", 130, 130, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '6') }, '6')
    val B7 : GObject = GButton(renderer, 2007, "7", 30, 180, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '7') }, '7')
    val B8 : GObject = GButton(renderer, 2008, "8", 80, 180, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '8') }, '8')
    val B9 : GObject = GButton(renderer, 3009, "9", 130, 180, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '9') }, '9')

    val Badd : GObject = GButton(renderer, 3110, "+", 180, 80, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '+') }, '+')
    val Bsub : GObject = GButton(renderer, 3111, "-", 180, 130, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '-') }, '-')
    val Bdiv : GObject = GButton(renderer, 3112, "/", 180, 180, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '/') }, '/')
    val Bmul : GObject = GButton(renderer, 3113, "*", 180, 230, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_button(obj, '*') }, '*')
    val Beq : GObject = GButton(renderer, 3200, "=", 130, 230, Color(164U,164U,164U), Color(0U,0U,0U), { obj -> Handler_eq(obj) }, '=')
    val Edit : GObject = GEdit(renderer, 5000, 30, 30, 240, Color(255U,255U,0U), Color(0U,0U,0U), null)

    Panel.Add(B0)
    Panel.Add(B1)
    Panel.Add(B2)
    Panel.Add(B3)
    Panel.Add(B4)
    Panel.Add(B5)
    Panel.Add(B6)
    Panel.Add(B7)
    Panel.Add(B8)
    Panel.Add(B9)
    Panel.Add(Badd)
    Panel.Add(Bsub)
    Panel.Add(Bdiv)
    Panel.Add(Bmul)
    Panel.Add(Beq)
    Panel.Add(Edit)

    while (!quit) {
        SDL_Delay(1000 / 60)
        memScoped {
            val event = alloc<SDL_Event>()
            while (SDL_PollEvent(event.ptr.reinterpret()) != 0) {
                val eventType = event.type
                when (eventType) {
                    //SDL_QUIT -> quit = true
                    SDL_MOUSEBUTTONDOWN -> {
                        if (event.button.button == SDL_BUTTON_LEFT.toUByte()){
                            xor_click = 0
                            Panel.HandleClickDown(Panel, event.motion.x, event.motion.y)
                        }
                    }
                    SDL_MOUSEBUTTONUP -> {
                        if (event.button.button == SDL_BUTTON_LEFT.toUByte()){
                            xor_click++
                            if (xor_click == 1)
                                Panel.HandleClickUp(Panel, event.motion.x, event.motion.y)
                        }
                    }
                    SDL_KEYDOWN -> {
                        val keyboardEvent = event.ptr.reinterpret<SDL_KeyboardEvent>().pointed
                        when (keyboardEvent.keysym.sym) {
                            SDLK_ESCAPE.toInt() -> quit=true
                            else -> Panel.HandleKey(keyboardEvent.keysym.sym, Panel)
                        }
                    }
                }
            }
        }
        SDL_SetRenderDrawColor(renderer, 242U, 242U, 242U, SDL_ALPHA_OPAQUE.toUByte())
        SDL_RenderClear(renderer)
        Panel.Paint()
        SDL_RenderPresent(renderer)
    }

    SDL_DestroyRenderer(renderer)
    SDL_DestroyWindow(window)
    SDL_Quit()
}
