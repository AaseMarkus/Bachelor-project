import React, { Component } from 'react'
import WeekMinimizeImage from './../../../assets/images/week-minimize.PNG'
import WeekMaximizeImage from './../../../assets/images/week-expand.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekMinimize extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Container>
                        <Row><Image src={WeekMinimizeImage} alt="Image of weekbox" width={200}/></Row>
                        <Row><Image src={WeekMaximizeImage} alt="Image of weekbox" width={200}/> </Row>
                    </Container>
                </Col>
                <Col>
                    <b><i>Minimiseringsknappen</i></b> brukes for å forminske en ukeboks slik at det blir mer 
                    plass til de andre ukeboksene.
                </Col>
            </Row>
        )
    }
}
