package com.github.samblake.meerkat.crumbs

import com.github.samblake.meerkat.edge.Database.query
import com.github.samblake.meerkat.model.NamedEntity
import com.github.samblake.meerkat.model.NamedEntityClass
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.routing.Route
import io.ktor.routing.RouteSelector
import io.ktor.routing.RouteSelectorEvaluation
import io.ktor.routing.RouteSelectorEvaluation.Companion.Constant
import io.ktor.routing.RoutingResolveContext
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.ContextDsl
import io.ktor.util.pipeline.PipelineContext

object Crumb {
    val crumbs = AttributeKey<MutableList<String>>("crumbs")
    val title = AttributeKey<String>("title")
    val entity = AttributeKey<NamedEntity<out Any>>("entity")
    val parent = AttributeKey<NamedEntity<out Any>>("parent")
}

@ContextDsl
fun Route.crumb(name: String, callback: Route.() -> Unit): Route {

    val routeWithCrumb = this.createChild(object : RouteSelector(1.0) {

        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation = Constant

        override fun toString(): String = "crumb($name)"

    })

    // Intercepts calls from this route at the features step
    routeWithCrumb.intercept(ApplicationCallPipeline.Features) {
        populateCrumbs(name)
    }

    // Configure this route with the block provided by the user
    callback(routeWithCrumb)

    return routeWithCrumb
}

@ContextDsl
fun <T:NamedEntity<out Any>>Route.crumb(entityClass: NamedEntityClass<T>, callback: Route.() -> Unit): Route {

    val routeWithCrumb = this.createChild(object : RouteSelector(1.0) {

        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation = Constant

    })

    // Intercepts calls from this route at the features step
    routeWithCrumb.intercept(ApplicationCallPipeline.Features) {
        val ids = context.request.call.parameters.getAll("id")
        val id = Integer.parseInt(ids?.last())
        query { entityClass.findById(id) } ?.let {
            context.request.call.attributes.getOrNull(Crumb.entity)?.let {
                context.request.call.attributes.put(Crumb.parent, it)
            }
            context.request.call.attributes.put(Crumb.entity, it)
            populateCrumbs(it.name)
        }
    }

    // Configure this route with the block provided by the user
    callback(routeWithCrumb)

    return routeWithCrumb
}

private fun PipelineContext<Unit, ApplicationCall>.populateCrumbs(name: String) {
    val crumbs = context.request.call.attributes.getOrNull(Crumb.crumbs) ?: ArrayList()
    crumbs.add(name)
    context.request.call.attributes.put(Crumb.crumbs, crumbs)
    context.request.call.attributes.put(Crumb.title, name)
}