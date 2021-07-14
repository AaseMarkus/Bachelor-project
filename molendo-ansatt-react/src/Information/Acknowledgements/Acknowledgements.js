import React, { Component } from 'react'
import { Container, Row, Col } from 'react-bootstrap'
import i18n from './../../Language/i18n'
import './Acknowledgements.css'

export default class Acknowledgements extends Component {
    render() {
        return (
            <Container className="acknowledgements">
                <Row><h1 id="acknowledgements">{i18n.t("information.acknowledgements-title")}</h1></Row>
                <Row><p>{i18n.t("information.acknowledgements-subtitle")}</p></Row>

                <Row><h4>Bootstrap React</h4></Row>
                <Row><a href="https://github.com/react-bootstrap/react-bootstrap" target="_blank">https://github.com/react-bootstrap</a></Row>
                <Row><p>Copyright &copy; 2021 Stephen J. Collings, Matthew Honnibal, Pieter Vanderwerff. The React Bootstrap framework is licensed under the <a href="https://opensource.org/licenses/MIT">MIT license</a>.</p></Row>

                <Row><h4>Ant Design Icons</h4></Row>
                <Row><a href="https://ant.design/" target="_blank">https://ant.design</a></Row>
                <Row><p>Copyright &copy; 2021 Ant UED, <a href="https://xtech.antfin.com/" target="_blank">https://xtech.antfin.com</a>. Ant Design icons is released under the <a href="https://opensource.org/licenses/MIT">MIT license</a>.</p></Row>

                <Row><h4>Google Material Design Icons</h4></Row>
                <Row><a href="https://fonts.google.com/icons" target="_blank">https://fonts.google.com/icons</a></Row>
                <Row><p>Provided under the <a href="https://www.apache.org/licenses/LICENSE-2.0.txt">Apache 2.0 license</a>.</p></Row>
                
                <Row><h4>Ionic Framework</h4></Row>
                <Row><a href="https://ionicons.com/" target="_blank">https://ionicons.com</a></Row>
                <Row><p>Copyright &copy; 2021 Ionic. Ionic icons are licensed under the <a href="https://opensource.org/licenses/MIT">MIT license</a>.</p></Row>

                <Row><h4>React Spinkit</h4></Row>
                <Row><a href="https://github.com/KyleAMathews/react-spinkit/" target="_blank">https://github.com/KyleAMathews/react-spinkit</a></Row>
                <Row><p>Copyright &copy; 2014 Kyle Mathews. The React Spinkit module is licensed under the <a href="https://opensource.org/licenses/MIT">MIT license</a>.</p></Row>

                <Row><h4>Feathericons</h4></Row>
                <Row><a href="https://github.com/feathericons/feather/" target="_blank">https://github.com/feathericons/feather</a></Row>
                <Row><p>Copyright &copy; 2017 Cole Bemis. Feathericons is licensed under the <a href="https://opensource.org/licenses/MIT">MIT license</a>.</p></Row>

            </Container>
        )
    }
}
