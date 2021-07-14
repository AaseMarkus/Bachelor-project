import React, { Component } from 'react'
import DayCopying from './DayCopying'
import WeekCopying from './WeekCopying'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class Copying extends Component {
    render() {
        return (
            <Container>
                <Row>
                    <h2>Kopiering</h2>
                </Row>
                
                <WeekCopying />
                <br />
                <DayCopying />
            </Container>
        )
    }
}
